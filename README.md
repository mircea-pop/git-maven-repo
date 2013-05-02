git-maven-repo
==============

Gradle plugin to publish a project into a maven based git repository. 
The plugin is cloning your git repository into a working directory and then is using the gradle maven plugin's 
`uploadArchives` task to publish your archives into the new git repository; this is followed by pushing the new commit 
to the provided git repository.

## Adding the Plugin

Add the following lines to your build to use the git-maven-repo plugin.

    buildscript {
      repositories { maven { url "http://epages-de.github.com/maven-repo/releases" } }
      dependencies { classpath 'com.epages.git:git-maven-repo:0.1' }
    }
    
Usage
=======
## Github Maven Repo Plugin

To apply the Github Maven Repo plugin add the following line to your build:

    apply plugin: 'git-maven-repo'

This configures tasks needed to clone, add, commit, and push changes to the
configured branch of your Github repository.

## Using Tasks

In order to use it, you have to define the `uploadArchives` task and then run
```
gradle publishGitRepo
```

The plugin is `snapshots` and `releases` aware.   

### Configuring Repository To Push To

The repository and the branch that the releases will be pushed to is configured via the
`githubRepo` extension:

```
githubRepo {
  gitMavenRepo = 'https://github.com/ePages-de/maven-repo.git'
  gitBranch = 'gh-pages'
}
```

The `gitBranch` configuration is optional, default beeing `gh-pages`

### Properties-Based Authentication

Beyond what is mentioned above, the plugin also provides a
file based way to authenticate.  If you are using username/password
credentials and don't want to re-enter them during each build, you can
specify the credentials in the `gradle.properties` file.  As these are
sensitive values, they should not be in the project's `gradle.properties`,
but rather in the user's `~/.gradle/gradle.properties`.

```
github.credentials.username = username
github.credentials.password = password
```

---
