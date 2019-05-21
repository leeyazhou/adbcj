package com.ly.train.flower.db.mysql.codec.packets.request;

import java.io.IOException;
import java.io.OutputStream;
import com.ly.train.flower.db.mysql.codec.packets.Command;


public class CommandRequest extends AbstractRequest {

  private final Command command;


  public CommandRequest(Command command) {
    super();
    this.command = command;
  }

  public Command getCommand() {
    return command;
  }

  @Override
  public int getLength() {
    return 1;
  }

  @Override
  protected boolean hasPayload() {
    return false;
  }

  @Override
  public final void writeToOutputStream(OutputStream out) throws IOException {
    out.write(command.getCode());
    if (hasPayload()) {
      writePayLoad(out);
    }
  }

  protected void writePayLoad(OutputStream out) throws IOException {
  }

  @Override
  public String toString() {
    return "CommandRequest{" + "command=" + command + '}';
  }
}

