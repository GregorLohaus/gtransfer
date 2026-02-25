package com.gregor_lohaus.gtransfer.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.gregor_lohaus.gtransfer.config.types.Config;
// import com.google.gson.Gson;
// import com.google.gson.GsonBuilder;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

import io.github.wasabithumb.jtoml.JToml;
import io.github.wasabithumb.jtoml.value.table.TomlTable;

public class ConfigEnvironmentPostProcessor implements EnvironmentPostProcessor {
	private static final Path CONFIG_FILE_PATH;
	static {
		Path sysPath = Paths.get("etc","gtransfer","config.toml");	
		Path userPath = Paths.get(System.getProperty("user.home"),".config","gtransfer","config.toml");
		if (Files.isReadable(sysPath) && !Files.isReadable(userPath)) {
			CONFIG_FILE_PATH = sysPath;	
		} else {
			CONFIG_FILE_PATH = userPath;	
		}	
	}

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		JToml toml = JToml.jToml();
		TomlTable table;
		if (Files.isReadable(CONFIG_FILE_PATH)) {
			table = toml.read(CONFIG_FILE_PATH);
		} else {
			try {
				Files.createDirectories(CONFIG_FILE_PATH.getParent());
				Files.createFile(CONFIG_FILE_PATH);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			Config defaultConfig = DefaultConfig.config;
			// Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
			// String json = gson.toJson(defaultConfig);
			// System.out.println(json);
			
			table = ConfigSerializer.toToml(defaultConfig);
			toml.write(CONFIG_FILE_PATH, table);
		}
		;
		Config config = new Config();
		config = ConfigSerializer.fromToml(table);
		ReflectionPropertySource<Config> source = new ReflectionPropertySource<Config>("gtransfer-config", config);
		environment.getPropertySources()
				.addLast(source);
	}

}
