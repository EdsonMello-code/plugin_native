import 'package:flutter_test/flutter_test.dart';
import 'package:plugin_native/plugin_native.dart';
import 'package:plugin_native/plugin_native_platform_interface.dart';
import 'package:plugin_native/plugin_native_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockPluginNativePlatform
    with MockPlatformInterfaceMixin
    implements PluginNativePlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final PluginNativePlatform initialPlatform = PluginNativePlatform.instance;

  test('$MethodChannelPluginNative is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelPluginNative>());
  });

  test('getPlatformVersion', () async {
    PluginNative pluginNativePlugin = PluginNative();
    MockPluginNativePlatform fakePlatform = MockPluginNativePlatform();
    PluginNativePlatform.instance = fakePlatform;

    expect(await pluginNativePlugin.getPlatformVersion(), '42');
  });
}
