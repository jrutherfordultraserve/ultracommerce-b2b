package com.mycompany.core.config;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.demo.AutoImportPersistenceUnit;
import org.broadleafcommerce.common.demo.AutoImportSql;
import org.broadleafcommerce.common.demo.AutoImportStage;
import org.broadleafcommerce.common.demo.ImportCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration("blPrivateDemoData")
@Conditional(ImportCondition.class)
public class ImportSQLConfig {

    public static final int BASIC_DATA_SPECIAL = AutoImportStage.PRIMARY_PRE_BASIC_DATA+300;

    @Bean
    public AutoImportSql blPrivateDemoThemeData() {
        return new AutoImportSql(AutoImportPersistenceUnit.BL_PU, "/sql/load_private_demo_theme_data.sql", BASIC_DATA_SPECIAL);
    }

    @Bean
    public AutoImportSql blPrivateDemoAccountAndCustomerDemoData() {
        String[] importFiles = new String[] {
                "config/bc/sql/samples/load_b2b_account_data.sql"};
        return new AutoImportSql(AutoImportPersistenceUnit.BL_PU, StringUtils.join(importFiles, ","), AutoImportStage.PRIMARY_POST_BASIC_DATA);
    }

}
