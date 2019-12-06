package net.whg.asj.packets;

public enum PacketType
{
    /**
     * This packet is sent from the client to the server when a client is first
     * connected. This is used to register an environment within the server for the
     * given user. If the user is validated, a token is returned which can be used
     * to send further packets. If the user is not validated, an INVALID_USER_LOGIN
     * packet is returned. <br>
     * <br>
     * username - The name of the user. Should contain only alphanumeric characters
     * and underscores. Cannot be left empty. <br>
     * <br>
     * password - The password of the user. If empty, the user attempts to login
     * without a password.
     */
    USER_CONNECTED("USER_CONNECTED", new String[] {"username", "password"}),

    /**
     * After a user is connected to the server and authenticated, a token for the
     * user is generated and returned to be paired with sending other packets. <br>
     * <br>
     * username - The name of the user who was authenticated. <br>
     * <br>
     * token - The API token generated by the server.
     */
    USER_AUTHENTICATED("USER_AUTHENTICATED", new String[] {"username", "token"}),

    /**
     * Returned by the server when a user attempts to connect with an invalid
     * username or password. <br>
     * <br>
     * username - The user who attempted to login.
     */
    INVALID_USER_LOGIN("INVALID_USER_LOGIN", new String[] {"username"}),

    /**
     * This packet is sent from the client to register a new command to the
     * namespace currently used by the user. <br>
     * <br>
     * cmd_name - The name of the command to register. <br>
     * <br>
     * namespace - The namespace to register the command in.
     */
    REGISTER_COMMAND("REGISTER_COMMAND", new String[] {"cmd_name", "namespace"}),

    /**
     * Sends a command to the server to be executed. This can be any executable
     * AwgenShell script. If the server is currently in the middle of executing a
     * command by this user, or otherwise bottle necked by another command, the
     * command is appended to the end of a queue. <br>
     * <br>
     * token - The token of the user sending the command. If not valid, the command
     * is not executed and an INVALID_TOKEN packet is returned. <br>
     * <br>
     * command - The command string to execute.
     */
    SEND_COMMAND("SEND_COMMAND", new String[] {"token", "command"}),

    /**
     * This packet is returned by the server when an invalid token is attached to a
     * packet. <br>
     * <br>
     * token - The token that was used.
     */
    INVALID_TOKEN("INVALID_TOKEN", new String[] {"token"}),

    /**
     * Sent from the server to the client to stream output text from the currently
     * executing command. This is intended to be written directly to the terminal.
     * Any number of output packets may be sent depending on whether the command is
     * streaming text or not. Output may be from any command sent by the user. <br>
     * <br>
     * token - The token of the user who sent the command. <br>
     * <br>
     * text - The text to be written to the terminal.
     */
    OUTPUT_TO_TERMINAL("OUTPUT_TO_TERMINAL", new String[] {"token", "text"}),

    /**
     * Called from the server to run a registered command. Multiple instances of
     * this can be called at once if multiple users request this command at the same
     * time. They may be handled syncrhonized or asynchronized. <br>
     * <br>
     * instance - A generated API token which represents this specific execution
     * instance for the purpose of returning generated content back to the server or
     * streamin incoming information back to this command. <br>
     * <br>
     * cmd_name - The name of the command to run. <br>
     * <br>
     * namespace - The namespace the command is located in. <br>
     * <br>
     * flags - A list of flags which were used with this command execution. This is
     * sent as a JSON object. <br>
     * <br>
     * args - A list of arguments which were used with this command execution. This
     * is sent as a JSON object.
     */
    RUN_COMMAND("RUN_COMMAND", new String[] {"instance", "cmd_name", "namespace", "flags", "args"});

    // INPUT_STREAM("INPUT_STREAM", new String[] {}),
    // COMMAND_EXIT("COMMAND_EXIT");
    // TERMINAL_INPUT("TERMINAL_INPUT"),
    // UNREGISTER_COMMAND("UNREGISTER_COMMMAND"),
    // UPDATE_ACTIVE_TEXT("UPDATE_ACTIVE_TEXT"),
    // CHANGE_NAMESPACE("CHANGE_NAMESPACE"),
    // FAILED_TO_REGISTER_COMMAND("FAILED_TO_REGISTER_COMMAND");

    private final String name;
    private final String[] properties;

    private PacketType(String name, String[] properties)
    {
        this.name = name;
        this.properties = properties;
    }

    /**
     * Gets the name of this packet.
     * 
     * @return The name of this packet.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets an array of all properties which are defined by this packet type.
     * 
     * @return An array of all properties.
     */
    public String[] getProperties()
    {
        return properties;
    }

    /**
     * Attempts to find the packet based on a given name. This function is not
     * case-sensitive.
     * 
     * @param s
     *     - The name of the packet.
     * @return The packet type with the given name.
     * @throws UnknownPacketException
     *     If there is no packet with the given name.
     */
    public static PacketType getFromName(String s)
    {
        s = s.toUpperCase();

        for (PacketType t : values())
            if (t.getName()
                 .equals(s))
                return t;

        throw new UnknownPacketException(s);
    }
}