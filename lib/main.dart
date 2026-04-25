import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:plantogether_app/app.dart';
import 'package:plantogether_app/core/utils/app_bloc_observer.dart';

// Entry point for the Patrol E2E test harness.
// Firebase is intentionally skipped — no GoogleService-Info.plist / google-services.json
// in the test project. Tests that need backend data use ApiHelper directly over HTTP.
void main() {
  WidgetsFlutterBinding.ensureInitialized();
  Bloc.observer = AppBlocObserver();
  runApp(const PlanTogetherApp());
}
