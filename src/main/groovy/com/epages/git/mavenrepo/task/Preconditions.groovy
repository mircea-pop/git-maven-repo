package com.epages.git.mavenrepo.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.publication.maven.internal.ant.BaseMavenDeployer
import org.gradle.api.tasks.TaskAction

public class Preconditions extends DefaultTask {

    @TaskAction
    public void check() {
        def repositories =  project.tasks.uploadArchives.repositories

        if (repositories.isEmpty()) {
            throw new GradleException("Cannot continue. Please configure the 'uploadArchives' task.")
        }

        def hasMavenDeployer = repositories.find() instanceof BaseMavenDeployer

        if (!hasMavenDeployer) {
            throw new GradleException("Cannot continue. The 'uploadArchives' task has no mavenDeployer defined.")
        }
    }
}