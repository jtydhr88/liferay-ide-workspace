# liferay-ide-workspace

## Building from source
If you would like to build from source, use this following command:

```
$ ./gradlew clean build
```

Once it finishes the locally built the Plugin Repository will be located here:

```
com.liferay.ide.workspace.site/build/repository
```

You can install this using _Help > Install New Software > Add... > Local..._ to point the repository folder


## Setup the development environment for this plugin project:
1. run gradle task:

```
$ ./gradlew clean installTargetPlatform
```


2. Import this project into Eclipse by using Gradle