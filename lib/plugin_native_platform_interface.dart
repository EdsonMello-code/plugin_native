import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'plugin_native_method_channel.dart';

abstract class PluginNativePlatform extends PlatformInterface {
  /// Constructs a PluginNativePlatform.
  PluginNativePlatform() : super(token: _token);

  static final Object _token = Object();

  static PluginNativePlatform _instance = MethodChannelPluginNative();

  /// The default instance of [PluginNativePlatform] to use.
  ///
  /// Defaults to [MethodChannelPluginNative].
  static PluginNativePlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [PluginNativePlatform] when
  /// they register themselves.
  static set instance(PluginNativePlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
