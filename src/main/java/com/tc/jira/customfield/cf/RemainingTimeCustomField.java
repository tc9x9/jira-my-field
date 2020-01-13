package com.tc.jira.customfields.cf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.component.ComponentAccessor;
// import com.atlassian.jira.issue.customfields.impl.TextCFType;
import com.atlassian.jira.issue.customfields.impl.NumberCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.customfields.converters.DoubleConverter;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;


@Scanned
public class RemainingTimeCustomField extends NumberCFType {

    private static final Logger log = LoggerFactory.getLogger(RemainingTimeCustomField.class);

    static final String PLUGIN_STORAGE_KEY = "com.tc.jira.customfield.adminui";

    private PluginSettingsFactory pluginSettingsFactory;

    public Object getPluginKey(String key) {
        return pluginSettingsFactory.createGlobalSettings().get(key);
    }

    public RemainingTimeCustomField(@JiraImport CustomFieldValuePersister customFieldValuePersister,
                                    @JiraImport DoubleConverter doubleConverter,
                                    @JiraImport GenericConfigManager genericConfigManager,
                                    @ComponentImport PluginSettingsFactory pluginSettingsFactory) {
      super(customFieldValuePersister, doubleConverter, genericConfigManager);
      this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public Map<String, Object> getVelocityParameters(final Issue issue,
                                                     final CustomField field,
                                                     final FieldLayoutItem fieldLayoutItem) {
        final Map<String, Object> map = super.getVelocityParameters(issue, field, fieldLayoutItem);

        // This method is also called to get the default value, in
        // which case issue is null so we can't use it to add currencyLocale
        if (issue == null) {
            return map;
        }

         FieldConfig fieldConfig = field.getRelevantConfig(issue);
         //add what you need to the map here
         // String s = (String) field.ge1tValue(issue);
         // String fieldValue = (String) issue.getCustomFieldValue(field);

         // https://community.atlassian.com/t5/Answers-Developer-Questions/How-to-check-if-jira-issue-is-resolved-programatically/qaq-p/520772
         if (issue.getResolutionDate() == null) {
           CustomField cF = ComponentAccessor.getCustomFieldManager().getCustomFieldObject((String) getPluginKey(PLUGIN_STORAGE_KEY + ".customField1"));
           if (cF != null) {
             Timestamp fieldValue = (Timestamp) issue.getCustomFieldValue(cF);
             if ( fieldValue != null ) {
               Timestamp todayValue = new Timestamp(System.currentTimeMillis());
               long diffInMillies = fieldValue.getTime() - todayValue.getTime();
               // if (diffInMillies < 0) {
               //   diffInMillies = 0;
               // }
               long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
               // map.put("cValue", Long.toString(diff));
               map.put("cValue", diff);
             }
           } else {
             map.put("cValue", 9999999);
           }
        }
        return map;
    }
}
