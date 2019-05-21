package com.ly.train.flower.db.h2.packets;

import com.ly.train.flower.db.api.support.SizeConstants;
import com.ly.train.flower.db.h2.protocol.CommandCodes;
import java.io.DataOutputStream;
import java.io.IOException;


public class UpdateExecute extends ClientToServerPacket {
  private final int id;
  private final Object[] params;
  private static final Object[] NO_PARAMS = new Object[0];

  public UpdateExecute(int id) {
    super();
    this.id = id;
    this.params = NO_PARAMS;
  }

  public UpdateExecute(int id, Object[] params) {
    super();
    this.id = id;
    this.params = params;
  }

  @Override
  public void writeToStream(DataOutputStream stream) throws IOException {
    stream.writeInt(CommandCodes.COMMAND_EXECUTE_UPDATE.getCommandValue());
    stream.writeInt(id);
    ParametersSerialisation.writeParams(stream, params);
  }

  @Override
  public int getLength() {
    return SizeConstants.INT_SIZE + // command execute update
        SizeConstants.INT_SIZE + // command id
        ParametersSerialisation.calculateParameterSize(params) + 0;
  }
}
