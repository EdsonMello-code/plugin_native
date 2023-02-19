
import 'plugin_native_platform_interface.dart';

class PluginNative {
  Future<String?> getPlatformVersion() {
    return PluginNativePlatform.instance.getPlatformVersion();
  }
}
