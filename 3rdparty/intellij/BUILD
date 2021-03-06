import os
import re


_jdk_jars = [
    'sa-jdi.jar',
    'tools.jar'
]


VERSION_PATTERN = r"""
  ^
  (((?P<name>[a-z0-9_\-]+)    # best effort to match name and version
    ((?:\-)
     (?P<version>
       [0-9\.]+
       (\-[a-zA-Z0-9]+)?
     )?
    )?
   )
   |
   (?P<name2>.+)              # otherwise use the entire basename minus suffix as name
  )
  (?:\.jar)                   # suffix
  $
"""


_regex = re.compile(VERSION_PATTERN, re.VERBOSE)


def extract_package_name_version(path):
  """Extract package name and version out of file basename.

  Matching is regular expression based using simple heuristics.

  A few examples:

    cglib-2.2.2.jar => cglib, 2.2.2
    markdownj-core-0.4.2-SNAPSHOT.jar => markdownj-core, 0.4.2-SNAPSHOT
    sanselan-0.98-snapshot.jar => sanselan, 0.98-snapshot

  When matches are not found, 'latest' is the default version and basename
  minus suffix is the package name. For example:

    junit-rt.jar => junit-rt, latest
    jsr173_1.0_api.jar => jsr173_1.0_api, latest

  """

  basename = os.path.basename(path)
  match = _regex.search(basename)
  if match:
    name = match.group('name') or match.group('name2') or basename
    version = match.group('version') or 'latest'
    return name, version
  return basename, 'latest'


def is_ultimate():
    return os.getenv('IJ_ULTIMATE', 'false') == 'true'


def _find_all_jars(root_dir):
    result = []
    for root, _, files in os.walk(root_dir):
        for file in files:
            if file.endswith('.jar'):
                result.append(os.path.join(root, file))
    return result


def _jar_path_in_lib_folder(base_dir, jar_name):
    lib_path = os.path.join(base_dir, 'lib')
    return _jar_path_in_folder(lib_path, jar_name)


def _jar_path_in_folder(lib_path, jar_name):
    jar_path = os.path.join(lib_path, jar_name)
    if not os.path.exists(jar_path):
        raise Exception('Jar %s does not exists!' % jar_path)
    return jar_path


def _intellij_sdk_classpath():
    base_dir = os.environ['INTELLIJ_HOME']
    if not os.path.exists(base_dir):
        raise Exception('Directory %s not exists!' % base_dir)

    def plugin_jar(path, name=None):
        package, version = extract_package_name_version(path)
        return jar(
            org='com.intellij.sdk.%s' % ('ultimate' if is_ultimate() else 'community'),
            name=package,
            rev=version,
            url='file://%s' % path,
        )

    intellij_jars = []

    # lib
    for file in os.listdir(os.path.join(base_dir, 'lib')):
        if file.endswith('.jar'):
            intellij_jars.append(plugin_jar(os.path.join(base_dir, 'lib', file)))

    return intellij_jars


def _jdk_classpath():
    jdk_libs_home = os.environ['JDK_LIBS_HOME']
    return [ _jar_path_in_folder(jdk_libs_home, jdk_jar) for jdk_jar in _jdk_jars ]


def _find_jars_for_plugin(plugin_name, *jar_names):
    base_dir = os.environ['INTELLIJ_PLUGINS_HOME']
    plugin_home = os.path.join(base_dir, plugin_name)
    if not os.path.exists(plugin_home):
        raise Exception('Directory %s not exists!' % plugin_home)
    return [
        _jar_path_in_lib_folder(plugin_home, jar_name) for jar_name in jar_names
    ]


def _intellij_sdk_jar(path):
    package, version = extract_package_name_version(path)
    return jar(
        org='com.intellij.sdk',
        name=package,
        rev=version,
        url='file://%s' % path,
    )


def _intellij_plugin_jar(path):
    package, version = extract_package_name_version(path)
    return jar(
        org='com.intellij.plugins',
        name=package,
        rev=version,
        url='file://%s' % path,
    )


def _jdk_jar(path):
    package, version = extract_package_name_version(path)
    return jar(
        org='jdk',
        name=package,
        rev=version,
        url='file://%s' % path,
    )


jar_library(
    name = 'sdk',
    jars = _intellij_sdk_classpath() + [ _jdk_jar(path) for path in _jdk_classpath() ]
)


jar_library(
    name = 'external-system-test-api',
    jars = [
        _intellij_plugin_jar(os.path.join(os.getcwd(), 'testData', 'external-system-test-api.jar'))
    ]
)


jar_library(
    name = 'python-plugin',
    jars = [
        _intellij_plugin_jar(path) for path in _find_jars_for_plugin('python', 'python.jar')
    ]
)


jar_library(
    name = 'scala-plugin',
    jars = [
        _intellij_plugin_jar(path) for path in _find_jars_for_plugin('Scala',
            'scala-plugin.jar', 'jps/scala-jps-plugin.jar', 'scala-play-2-jps-plugin.jar', 'compiler-settings.jar'
        )
    ]
)


jar_library(
    name = 'junit-plugin',
    jars = [
        _intellij_plugin_jar(path) for path in _find_all_jars(
            os.path.join(os.environ['INTELLIJ_HOME'], 'plugins', 'junit', 'lib')
        )
    ]
)