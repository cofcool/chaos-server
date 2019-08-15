/*
 * Copyright 2019 cofcool
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.cofcool.chaos.server.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;

/**
 * 根据 git patch 文件批量替换
 *
 * 如下配置:
 *
 * <p>
 *     --patch=xx --path=xx --config=xx.json
 * </p>
 *
 * 配置文件格式:
 *
 * <pre>
 *{
 * 	"origin": {
 * 		"packageName": "xx",
 * 		"bean": "xx",
 * 		"beanField": "xx",
 * 		"comment": "xx"
 *        },
 * 	"newItems": [
 *        {
 * 			"packageName": "xx",
 * 			"bean": "xx",
 * 			"beanField": "xx",
 * 			"comment": "xx"
 *        }
 * 	]
 * }
 * </pre>
 * git 相关命令:
 * <pre>
 * git format-patch [commit]
 * git am *.patch
 * </pre>
 */
public class GenerateCode {

    public static void main(String[] args) throws IOException {
        ApplicationArguments arguments = new DefaultApplicationArguments(args);

        GenerateCode generateCode = new GenerateCode(
            arguments.getOptionValues(RESULT_PATH).get(0),
            arguments.getOptionValues(PATCH_PATH).get(0),
            arguments.getOptionValues(INPUT_CONFIG).get(0)
        );
        generateCode.generatePatch();
    }


    /**
     * git patch 文件路径
     */
    private static final String PATCH_PATH = "patch";
    /**
     * 生成的 patch 文件保存路径
     */
    private static final String RESULT_PATH = "path";
    /**
     * 配置替换字符文件所在路径
     */
    private static final String INPUT_CONFIG = "config";


    private BufferedReader reader;

    private String resultPath;
    private String patchPath;
    private String configPath;

    private Configuration configuration;


    public GenerateCode(String resultPath, String patchPath, String configPath) throws IOException {
        this.resultPath = resultPath;
        this.patchPath = patchPath;
        this.configPath = configPath;

        setupConfigMap();
        this.reader = getBufferedReader(patchPath);
    }

    private void setupConfigMap() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        configuration = mapper.readValue(new File(configPath), Configuration.class);
    }


    public void generatePatch() throws IOException {
        File file = new File(resultPath);
        file.mkdirs();

        for (ConfigItem item : configuration.newItems) {
            reader = getBufferedReader(patchPath);
            replacePatch(item);
        }
    }

    private void replacePatch(ConfigItem configItem) throws IOException {
        StringBuilder newStrs = new StringBuilder();

        String lineStr;
        while ((lineStr = reader.readLine()) != null) {
            newStrs.append(replaceStr(lineStr, configItem)).append("\n");
        }

        writePatch(newStrs.toString(), configItem.bean, resultPath);
    }

    private void writePatch(String string, String fileName, String patchPath) throws FileNotFoundException {
        FileOutputStream outputStream = new FileOutputStream(new File(patchPath + File.separator + fileName + ".patch"));

        PrintStream ps = new PrintStream(outputStream);
        ps.append(string);

        ps.close();
    }

    private BufferedReader getBufferedReader(String patchPath) throws FileNotFoundException {
        return new BufferedReader(new FileReader(new File(patchPath)));
    }

    private String replaceStr(String originStr, ConfigItem item) {
        return originStr.replace(configuration.origin.getPackageName(), item.getPackageName())
                .replace(configuration.origin.getBean(), item.getBean())
                .replace(configuration.origin.getBeanField(), item.getBean().toLowerCase())
                .replace(configuration.origin.getComment(), item.getComment());
    }

    public static class Configuration {
        private ConfigItem origin;
        private List<ConfigItem> newItems;

        public ConfigItem getOrigin() {
            return origin;
        }

        public void setOrigin(ConfigItem origin) {
            this.origin = origin;
        }

        public List<ConfigItem> getNewItems() {
            return newItems;
        }

        public void setNewItems(
            List<ConfigItem> newItems) {
            this.newItems = newItems;
        }
    }

    public static class ConfigItem {
        private String packageName;
        private String bean;
        private String beanField;
        private String comment;

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getBean() {
            return bean;
        }

        public void setBean(String bean) {
            this.bean = bean;
        }

        public String getBeanField() {
            return beanField;
        }

        public void setBeanField(String beanField) {
            this.beanField = beanField;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}