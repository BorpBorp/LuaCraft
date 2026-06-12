package com.luacraft.test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class TestRunner {
    private static final String FILL_BUILD_ENDPOINT = "https://fill.papermc.io/v3/projects/paper/versions/1.21.4/builds/latest";
    private static final Path DESTINATION_PAPERCLIP_JAR = Paths.get("build/test_environment/paperclip.jar");
    private static final Path DESTINATION_PLUGIN_JAR = Paths.get("build/test_environment/plugins/LuaCraft.jar");
    private static final Path SOURCE_TESTS = Paths.get("src/main/resources/tests/");
    private static final Path DESTINATION_TESTS = Paths.get("build/test_environment/plugins/LuaCraft/scripts/");
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        setupTestEnvironment();
        runTests();
    }

    private static void setupTestEnvironment() throws IOException, InterruptedException, URISyntaxException {
        if (Files.exists(DESTINATION_PAPERCLIP_JAR)) {
            System.out.println("Paperclip already exists, skipping download.");
        } else {
            Files.createDirectories(DESTINATION_PAPERCLIP_JAR.getParent());
            downloadJar(URI.create(FILL_BUILD_ENDPOINT));
        }

        Files.createDirectories(DESTINATION_PLUGIN_JAR.getParent());
        Path pluginJarSource = new File(TestRunner.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toPath();
        Files.copy(pluginJarSource, DESTINATION_PLUGIN_JAR, StandardCopyOption.REPLACE_EXISTING);

        if (Files.exists(DESTINATION_TESTS)) {
            try (Stream<Path> paths = Files.walk(DESTINATION_TESTS)) {
                paths.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        }
        Files.createDirectories(DESTINATION_TESTS);
        System.out.println("Copying test scripts...");

        try (Stream<Path> paths = Files.walk(SOURCE_TESTS)) {
            paths.forEach((sourcePath) -> {
                Path relativePath = SOURCE_TESTS.relativize(sourcePath);
                Path destinationPath = DESTINATION_TESTS.resolve(relativePath);
                if (Files.isDirectory(sourcePath)) {
                    try {
                        Files.createDirectories(destinationPath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
                try {
                    Files.copy(sourcePath, destinationPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static void downloadJar(URI source) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest buildRequest = HttpRequest.newBuilder()
                .uri(source)
                .build();
        HttpResponse<InputStream> buildResponse = client.send(buildRequest, HttpResponse.BodyHandlers.ofInputStream());
        JsonObject buildObject;
        try (InputStreamReader reader = new InputStreamReader(buildResponse.body())) {
            buildObject = gson.fromJson(reader, JsonObject.class);
        }
        String downloadUrl = buildObject.get("downloads").getAsJsonObject()
                .get("server:default").getAsJsonObject()
                .get("url").getAsString();

        HttpRequest downloadRequest = HttpRequest.newBuilder()
                .uri(URI.create(downloadUrl))
                .build();
        HttpResponse<InputStream> downloadResponse = client.send(downloadRequest, HttpResponse.BodyHandlers.ofInputStream());
        try (InputStream inputStream = downloadResponse.body()) {
            Files.copy(inputStream, TestRunner.DESTINATION_PAPERCLIP_JAR);
        }
    }

    private static void runTests() throws IOException, InterruptedException {
        List<String> args = new ArrayList<>();
        args.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        args.add("-Dcom.mojang.eula.agree=true");
        args.add("-Dluacraft.testing.enabled=true");
        args.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000");
        args.add("-jar");
        args.add(TestRunner.DESTINATION_PAPERCLIP_JAR.getFileName().toString());
        args.add("--nogui");

        Process process = new ProcessBuilder(args)
                .directory(TestRunner.DESTINATION_PAPERCLIP_JAR.getParent().toFile())
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .redirectInput(ProcessBuilder.Redirect.INHERIT)
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.err.println("Paperclip process exited with code " + exitCode);
        } else {
            System.out.println("Paperclip process completed successfully.");
        }
    }
}
