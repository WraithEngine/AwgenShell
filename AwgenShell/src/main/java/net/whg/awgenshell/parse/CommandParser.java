package net.whg.awgenshell.parse;

import net.whg.awgenshell.exec.ShellEnvironment;

/**
 * The functional class used to parse inputs.
 *
 * @author TheDudeFromCI
 */
public class CommandParser
{
	/**
	 * Parses an input string into the an input function. This function handles all
	 * tokenization, and variable intialization as needed.
	 *
	 * @param environment
	 *     - The environment to compile the command in.
	 * @param line
	 *     - The line to parse.
	 * @return A compiled input function which can be executed within the given
	 *     environment.
	 */
	public static Input parse(ShellEnvironment environment, String line)
	{
		Tokenizer tokenizer = new Tokenizer(line);

		Input input = Input.consumeTokens(environment, tokenizer);

		if (tokenizer.hasNextToken())
			throw new CommandParseException("Unexpected token!", tokenizer.nextToken());

		if (input == null)
			input = new Input(environment);

		return input;
	}
}
