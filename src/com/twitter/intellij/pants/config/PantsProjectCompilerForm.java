// Copyright 2014 Pants project contributors (see CONTRIBUTORS.md).
// Licensed under the Apache License, Version 2.0 (see LICENSE).

package com.twitter.intellij.pants.config;

import javax.swing.*;

public class PantsProjectCompilerForm {
  private JPanel myMainPanel;
  private JCheckBox myUseIdeaProjectJdkCheckBox;
  private JCheckBox myUsePantsMakeBeforeRun;

  public PantsProjectCompilerForm() {
  }

  public JPanel getMainPanel() {
    return myMainPanel;
  }

  public boolean isUseIdeaProjectJdk() {
    return myUseIdeaProjectJdkCheckBox.isSelected();
  }

  public void setUseIdeaProjectJdk(boolean useIdeaProjectJdk) {
    myUseIdeaProjectJdkCheckBox.setSelected(useIdeaProjectJdk);
  }

  public boolean isUsePantsMakeBeforeRun() {
    return myUsePantsMakeBeforeRun.isSelected();
  }

  public void setUsePantsMakeBeforeRun(boolean usePantsMakeBeforeRun) {
    myUsePantsMakeBeforeRun.setSelected(usePantsMakeBeforeRun);
  }
}
