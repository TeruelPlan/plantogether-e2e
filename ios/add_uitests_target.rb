require 'xcodeproj'

project_path = File.expand_path('../ios/Runner.xcodeproj', __dir__)
project = Xcodeproj::Project.open(project_path)

# Skip if target already exists
if project.targets.any? { |t| t.name == 'RunnerUITests' }
  puts 'RunnerUITests target already exists, skipping.'
  exit 0
end

runner = project.targets.find { |t| t.name == 'Runner' }
raise 'Runner target not found' unless runner

# Create UI test target
ui_tests = project.new_target(:ui_test_bundle, 'RunnerUITests', :ios, '13.0')
ui_tests.add_dependency(runner)

# Add source file
group = project.main_group.find_subpath('RunnerUITests', true)
group.set_source_tree('<group>')
group.set_path('RunnerUITests')

FileUtils.mkdir_p(File.join(File.dirname(project_path), 'RunnerUITests'))
m_file_path = File.join(File.dirname(project_path), 'RunnerUITests', 'RunnerUITests.m')
File.write(m_file_path, <<~OBJC)
  #import <XCTest/XCTest.h>
  #import <patrol/patrol.h>
  #import <objc/runtime.h>

  PATROL_INTEGRATION_TEST_IOS_RUNNER(RunnerUITests)
OBJC

file_ref = group.new_reference(m_file_path)
file_ref.set_source_tree('<group>')
file_ref.path = 'RunnerUITests.m'
ui_tests.source_build_phase.add_file_reference(file_ref)

# Add xcode_backend build phases
build_phase = ui_tests.new_shell_script_build_phase('xcode_backend build')
build_phase.shell_script = '/bin/sh "$FLUTTER_ROOT/packages/flutter_tools/bin/xcode_backend.sh" build'

embed_phase = ui_tests.new_shell_script_build_phase('xcode_backend embed_and_thin')
embed_phase.shell_script = '/bin/sh "$FLUTTER_ROOT/packages/flutter_tools/bin/xcode_backend.sh" embed_and_thin'

# Match deployment target with Runner
runner_deployment = runner.build_settings['IPHONEOS_DEPLOYMENT_TARGET']
ui_tests.build_configurations.each do |config|
  config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = runner_deployment || '13.0'
  config.build_settings['TEST_TARGET_NAME'] = 'Runner'
  config.build_settings['BUNDLE_LOADER'] = ''
  config.build_settings['SWIFT_VERSION'] = '5.0'
end

project.save
puts 'RunnerUITests target added successfully.'
