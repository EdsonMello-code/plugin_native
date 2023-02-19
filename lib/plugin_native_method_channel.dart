import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'plugin_native_platform_interface.dart';

/// An implementation of [PluginNativePlatform] that uses method channels.
class MethodChannelPluginNative extends PluginNativePlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('plugin_native');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
