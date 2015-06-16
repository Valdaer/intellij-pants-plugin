// Copyright 2015 Pants project contributors (see CONTRIBUTORS.md).
// Licensed under the Apache License, Version 2.0 (see LICENSE).

package com.twitter.intellij.pants.execution;

import com.intellij.execution.JavaRunConfigurationExtensionManager;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfiguration;
import com.intellij.openapi.externalSystem.util.ExternalSystemConstants;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.testIntegration.TestIntegrationUtils;
import com.twitter.intellij.pants.model.PantsTargetAddress;
import com.twitter.intellij.pants.util.PantsConstants;
import com.twitter.intellij.pants.util.PantsUtil;

import java.util.Collections;

public class PantsTestRunConfigurationProducer extends RunConfigurationProducer<ExternalSystemRunConfiguration> {
  protected PantsTestRunConfigurationProducer() {
    super(PantsExternalTaskConfigurationType.getInstance());
  }

  @Override
  protected boolean setupConfigurationFromContext(
    ExternalSystemRunConfiguration configuration,
    ConfigurationContext context,
    Ref<PsiElement> sourceElement
  ) {
    final PantsTargetAddress targetAddress = PantsUtil.getTargetAddressFromModule(context.getModule());
    if (targetAddress == null) {
      return false;
    }

    configuration.getSettings().setExternalProjectPath(context.getModule().getOptionValue(ExternalSystemConstants.LINKED_PROJECT_PATH_KEY));
    configuration.getSettings().setTaskNames(Collections.singletonList("test"));

    final PsiElement psiLocation = context.getPsiLocation();
    final PsiClass psiClass = psiLocation != null ? TestIntegrationUtils.findOuterClass(psiLocation) : null;
    if (psiClass != null && TestIntegrationUtils.isTest(psiClass)) {
      configuration.setName("Test " + psiClass.getName());
      configuration.getSettings().setScriptParameters("--test-junit-test=" + psiClass.getQualifiedName());
    } else {
      configuration.setName("Test " + targetAddress.getRelativePath() + ":" + targetAddress.getTargetName());
    }

    JavaRunConfigurationExtensionManager.getInstance().extendCreatedConfiguration(configuration, context.getLocation());
    return true;
  }

  @Override
  public boolean isConfigurationFromContext(
    ExternalSystemRunConfiguration configuration,
    ConfigurationContext context
  ) {
    final ExternalSystemRunConfiguration tempConfig = new ExternalSystemRunConfiguration(
      PantsConstants.SYSTEM_ID, context.getProject(), configuration.getFactory(), configuration.getName()
    );
    setupConfigurationFromContext(tempConfig, context, null);
    return configuration.getSettings().equals(tempConfig.getSettings());
  }
}