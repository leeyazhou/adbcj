package com.ly.train.flower.db.h2.packets;


import java.io.DataOutputStream;
import java.io.IOException;
import com.ly.train.flower.db.api.support.SizeConstants;


public final class CloseCommand extends ClientToServerPacket {

  public CloseCommand() {
    super();
  }

  public static final int SESSION_CLOSE = 1;

  @Override
  public void writeToStream(DataOutputStream stream) throws IOException {
    stream.writeInt(SESSION_CLOSE);
  }

  @Override
  public int getLength() {
    return SizeConstants.INT_SIZE;
  }
}
