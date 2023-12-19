package com.autonomousapps.jvm

import com.autonomousapps.jvm.projects.GraphViewCacheProject
import org.gradle.util.GradleVersion

import static com.autonomousapps.kit.truth.TestKitTruth.assertThat
import static com.autonomousapps.utils.Runner.build

final class GraphViewCacheSpec extends AbstractJvmSpec {

  def "graphViewTask is sensitive to changing dependencies"() {
    given:
    def project = new GraphViewCacheProject()
    gradleProject = project.gradleProject
    def task = ':proj:graphViewMain'
    def gradleVersion = GradleVersion.current()

    when: 'First build'
    def result = build(gradleVersion, gradleProject.rootDir, task, '--build-cache', '-Dv=0.3.0-alpha27')

    then: 'Task executed'
    assertThat(result).task(task).succeeded()

    when: 'Second build'
    result = build(gradleVersion, gradleProject.rootDir, 'clean', task, '--build-cache', '-Dv=0.3.0-alpha28')

    then: 'Task executed (not FROM_CACHE)'
    assertThat(result).task(task).succeeded()
  }
}
