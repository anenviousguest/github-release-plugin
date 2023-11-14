package de.jutzig.github.release.plugin;

import java.util.stream.Stream;

import org.apache.maven.model.Scm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import static org.junit.jupiter.api.Assertions.*;

class UploadMojoTest {
	@ParameterizedTest(name = "{0} should resolve to {1} repository id")
	@CsvSource({
		"scm:git:https://github.com/jutzig/github-release-plugin.git, jutzig/github-release-plugin",
		"scm:git|https://github.com/jutzig/github-release-plugin.git, jutzig/github-release-plugin",
		"https://github.com/jutzig/github-release-plugin.git, jutzig/github-release-plugin",

		"scm:git:http://github.com/jutzig/github-release-plugin.git, jutzig/github-release-plugin",
		"scm:git|http://github.com/jutzig/github-release-plugin.git, jutzig/github-release-plugin",
		"http://github.com/jutzig/github-release-plugin.git, jutzig/github-release-plugin",

		"scm:git:git@github.com:jutzig/github-release-plugin.git, jutzig/github-release-plugin",
		"scm:git|git@github.com:jutzig/github-release-plugin.git, jutzig/github-release-plugin",
		"git@github.com:jutzig/github-release-plugin.git, jutzig/github-release-plugin",

		"scm:git:https://github.com/jutzig/github-release-plugin, jutzig/github-release-plugin",
		"scm:git|https://github.com/jutzig/github-release-plugin, jutzig/github-release-plugin",
		"https://github.com/jutzig/github-release-plugin, jutzig/github-release-plugin",

		"scm:git:http://github.com/jutzig/github-release-plugin.git/child, jutzig/github-release-plugin",
		"scm:git|http://github.com/jutzig/github-release-plugin.git/child, jutzig/github-release-plugin",
		"http://github.com/jutzig/github-release-plugin.git/child, jutzig/github-release-plugin"
	})
	void testComputeRepositoryId(String scmString, String expectedRepositoryId) {
		assertEquals(expectedRepositoryId, UploadMojo.computeRepositoryId(scmString));
	}

	@ParameterizedTest(name = "{0} should resolve to {1} endpoiont")
	@MethodSource("scmFixture")
	void testGithubEndpoint(Scm scm, String expectedEndpoint) {
		assertEquals(expectedEndpoint, UploadMojo.computeGithubApiEndpoint(scm));
	}

	@Test
	void testGuessPreRelease() {
		assertTrue(UploadMojo.guessPreRelease("1.0-SNAPSHOT"));
		assertTrue(UploadMojo.guessPreRelease("1.0-alpha"));
		assertTrue(UploadMojo.guessPreRelease("1.0-alpha-1"));
		assertTrue(UploadMojo.guessPreRelease("1.0-beta"));
		assertTrue(UploadMojo.guessPreRelease("1.0-beta-1"));
		assertTrue(UploadMojo.guessPreRelease("1.0-RC"));
		assertTrue(UploadMojo.guessPreRelease("1.0-RC1"));
		assertTrue(UploadMojo.guessPreRelease("1.0-rc1"));
		assertTrue(UploadMojo.guessPreRelease("1.0-rc-1"));

		assertFalse(UploadMojo.guessPreRelease("1"));
		assertFalse(UploadMojo.guessPreRelease("1.0"));
	}

	private static Stream<Arguments> scmFixture() {
		return Stream.of(
			// Public GitHub
			Arguments.of(mockScmWithConnectionString("scm:git:https://github.com/jutzig/github-release-plugin.git"), "https://api.github.com"),
			Arguments.of(mockScmWithConnectionString("scm:git|https://github.com/jutzig/github-release-plugin.git"), "https://api.github.com"),
			Arguments.of(mockScmWithConnectionString("https://github.com/jutzig/github-release-plugin.git"), "https://api.github.com"),

			Arguments.of(mockScmWithConnectionString("scm:git:http://github.com/jutzig/github-release-plugin.git"), "https://api.github.com"),
			Arguments.of(mockScmWithConnectionString("scm:git|http://github.com/jutzig/github-release-plugin.git"), "https://api.github.com"),
			Arguments.of(mockScmWithConnectionString("http://github.com/jutzig/github-release-plugin.git"), "https://api.github.com"),

			Arguments.of(mockScmWithConnectionString("scm:git:git@github.com:jutzig/github-release-plugin.git"), "https://api.github.com"),
			Arguments.of(mockScmWithConnectionString("scm:git|git@github.com:jutzig/github-release-plugin.git"), "https://api.github.com"),
			Arguments.of(mockScmWithConnectionString("git@github.com:jutzig/github-release-plugin.git"), "https://api.github.com"),

			Arguments.of(mockScmWithConnectionString("scm:git:https://github.com/jutzig/github-release-plugin"), "https://api.github.com"),
			Arguments.of(mockScmWithConnectionString("scm:git|https://github.com/jutzig/github-release-plugin"), "https://api.github.com"),
			Arguments.of(mockScmWithConnectionString("https://github.com/jutzig/github-release-plugin"), "https://api.github.com"),

			Arguments.of(mockScmWithConnectionString("scm:git:http://github.com/jutzig/github-release-plugin.git/child"), "https://api.github.com"),
			Arguments.of(mockScmWithConnectionString("scm:git|http://github.com/jutzig/github-release-plugin.git/child"), "https://api.github.com"),
			Arguments.of(mockScmWithConnectionString("http://github.com/jutzig/github-release-plugin.git/child"), "https://api.github.com"),

			// GitHub Enterprise
			Arguments.of(mockScmWithConnectionString("scm:git:https://github.acme.com/jutzig/github-release-plugin.git"), "https://github.acme.com/api/v3"),
			Arguments.of(mockScmWithConnectionString("scm:git|https://github.acme.com/jutzig/github-release-plugin.git"), "https://github.acme.com/api/v3"),
			Arguments.of(mockScmWithConnectionString("https://github.acme.com/jutzig/github-release-plugin.git"), "https://github.acme.com/api/v3"),

			Arguments.of(mockScmWithConnectionString("scm:git:http://github.acme.com/jutzig/github-release-plugin.git"), "http://github.acme.com/api/v3"),
			Arguments.of(mockScmWithConnectionString("scm:git|http://github.acme.com/jutzig/github-release-plugin.git"), "http://github.acme.com/api/v3"),
			Arguments.of(mockScmWithConnectionString("http://github.acme.com/jutzig/github-release-plugin.git"), "http://github.acme.com/api/v3"),

			Arguments.of(mockScmWithConnectionString("scm:git:git@github.acme.com:jutzig/github-release-plugin.git"), "https://github.acme.com/api/v3"),
			Arguments.of(mockScmWithConnectionString("scm:git|git@github.acme.com:jutzig/github-release-plugin.git"), "https://github.acme.com/api/v3"),
			Arguments.of(mockScmWithConnectionString("git@github.acme.com:jutzig/github-release-plugin.git"), "https://github.acme.com/api/v3"),

			Arguments.of(mockScmWithConnectionString("scm:git:https://github.acme.com/jutzig/github-release-plugin"), "https://github.acme.com/api/v3"),
			Arguments.of(mockScmWithConnectionString("scm:git|https://github.acme.com/jutzig/github-release-plugin"), "https://github.acme.com/api/v3"),
			Arguments.of(mockScmWithConnectionString("https://github.acme.com/jutzig/github-release-plugin"), "https://github.acme.com/api/v3"),

			Arguments.of(mockScmWithConnectionString("scm:git:http://github.acme.com/jutzig/github-release-plugin.git/child"), "http://github.acme.com/api/v3"),
			Arguments.of(mockScmWithConnectionString("scm:git|http://github.acme.com/jutzig/github-release-plugin.git/child"), "http://github.acme.com/api/v3"),
			Arguments.of(mockScmWithConnectionString("http://github.acme.com/jutzig/github-release-plugin.git/child"), "http://github.acme.com/api/v3"),

			// Fallback to public
			Arguments.of(null, "https://api.github.com")
		);
	}

	private static Scm mockScmWithConnectionString(String connection) {
		Scm scm = new Scm();
		scm.setConnection(connection);
		return scm;
	}
}
