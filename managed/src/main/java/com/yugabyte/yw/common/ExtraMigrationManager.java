// Copyright (c) YugaByte, Inc.

package com.yugabyte.yw.common;

import static com.yugabyte.yw.commissioner.Common.CloudType.onprem;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.yugabyte.yw.models.AccessKey;
import com.yugabyte.yw.models.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This is a manager to hold injected resources needed for extra migrations.
 */
@Singleton
public class ExtraMigrationManager extends DevopsBase {

  public static final Logger LOG = LoggerFactory.getLogger(ExtraMigrationManager.class);

  @Inject TemplateManager templateManager;

  @Override
  protected String getCommandType() {
    return "";
  }

  private void recreateProvisionScripts() {
    for (AccessKey accessKey : AccessKey.getAll()) {
      Provider p = Provider.get(accessKey.getProviderUUID());
      if (p != null && p.code.equals(onprem.name())) {
        templateManager.createProvisionTemplate(
            accessKey,
            p.details.airGapInstall,
            p.details.passwordlessSudoAccess,
            p.details.installNodeExporter,
            p.details.nodeExporterPort,
            p.details.nodeExporterUser,
            p.details.setUpChrony,
            p.details.ntpServers);
      }
    }
  }

  public void V52__Update_Access_Key_Create_Extra_Migration() {
    recreateProvisionScripts();
  }

  public void R__Recreate_Provision_Script_Extra_Migrations() {
    recreateProvisionScripts();
  }
}
