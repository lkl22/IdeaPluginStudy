package com.lkl.plugin.codegenerator.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@State(name = "CodeGeneratorSettings", storages = {@Storage("$APP_CONFIG$/CodeGeneratorSettings.xml")})
public class CodeGeneratorSettings implements PersistentStateComponent<CodeGeneratorSettings> {

    private static final Logger LOGGER = Logger.getInstance(CodeGeneratorSettings.class);
    private List<CodeTemplate> codeTemplates;

    public CodeGeneratorSettings() {

    }

    public CodeGeneratorSettings setCodeTemplates(List<CodeTemplate> codeTemplates) {
        this.codeTemplates = codeTemplates;
        return this;
    }


    @Nullable @Override public CodeGeneratorSettings getState() {
        if (codeTemplates == null) {
            codeTemplates = loadDefaultTemplates();
        }
        return this;
    }

    @Override public void loadState(CodeGeneratorSettings codeGeneratorSettings) {
        XmlSerializerUtil.copyBean(codeGeneratorSettings, this);
    }

    public List<CodeTemplate> getCodeTemplates() {
        if (codeTemplates == null) {
            codeTemplates = loadDefaultTemplates();
        }
        return codeTemplates;
    }

    public Optional<CodeTemplate> getCodeTemplate(String templateId) {
        return codeTemplates.stream()
                .filter(t -> t!= null && t.getId().equals(templateId))
                .findFirst();
    }

    public void removeCodeTemplate(String templateId) {
        codeTemplates.removeIf(template -> template.name.equals(templateId));
    }

    private List<CodeTemplate> loadDefaultTemplates() {
        List<CodeTemplate> templates = new ArrayList<>();
        try {
            templates.addAll(loadTemplates("getters-and-setters.xml"));
            templates.addAll(loadTemplates("to-string.xml"));
            templates.addAll(loadTemplates("HUE-Serialization.xml"));
        } catch (Exception e) {
            LOGGER.error("loadDefaultTemplates failed", e);
        }
        return templates;
    }

    private List<CodeTemplate> loadTemplates(String templateFileName) throws IOException {
        return CodeTemplateList.fromXML(FileUtil.loadTextAndClose(CodeGeneratorSettings.class.getResourceAsStream("/template/" + templateFileName)));
    }
}