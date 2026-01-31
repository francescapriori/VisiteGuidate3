package it.unibs.ingdsw.view.cli.io;

import java.io.PrintStream;

public class ConsoleOutput implements Output {

    private final PrintStream out;

    public ConsoleOutput(PrintStream out) {
        this.out = out;
    }

    @Override
    public void println(String s) {
        System.out.println(s);
    }
}
