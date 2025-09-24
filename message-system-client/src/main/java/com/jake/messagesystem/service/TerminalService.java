package com.jake.messagesystem.service;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.nio.file.Paths;

public class TerminalService {
    private final Terminal terminal;
    private final LineReader lineReader;

    private TerminalService(Terminal terminal, LineReader lineReader) {
        this.terminal = terminal;
        this.lineReader = lineReader;
    }

    public static TerminalService create() throws IOException {
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();

        LineReader lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .variable(LineReader.HISTORY_FILE, Paths.get("./data/history.txt"))
                .build();

        return new TerminalService(terminal, lineReader);
    }

    /**
     * 입력 줄을 읽되, 사용자가 친 입력은 화면에 남지 않는다.
     */
    public String readLine(String prompt) {
        String input = lineReader.readLine(prompt, (char) 0);

        // ✅ 입력 줄 지우기
        terminal.puts(org.jline.utils.InfoCmp.Capability.cursor_up);
        terminal.puts(org.jline.utils.InfoCmp.Capability.delete_line);
        terminal.flush();

        return input;
    }

    /** 일반 메시지 출력 (입력창 위에 쌓임). */
    public void printMessage(String username, String content) {
        lineReader.printAbove(String.format("%s : %s", username, content));
    }

    /** 시스템 메시지 출력 (입력창 위에 쌓임). */
    public void printSystemMessage(String content) {
        lineReader.printAbove("=> " + content);
    }

    /** 터미널 전체 클리어. */
    public void clearTerminal() {
        terminal.puts(org.jline.utils.InfoCmp.Capability.clear_screen);
        terminal.flush();
    }
}
