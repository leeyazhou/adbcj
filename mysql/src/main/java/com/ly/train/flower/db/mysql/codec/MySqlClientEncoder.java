package com.ly.train.flower.db.mysql.codec;

import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import com.ly.train.flower.db.mysql.codec.packets.request.AbstractRequest;

public class MySqlClientEncoder {

  public void encode(AbstractRequest request, OutputStream out) throws IOException, NoSuchAlgorithmException {
    int length = request.getLength();

    // Write the length of the packet
    out.write(length & 0xFF);
    out.write(length >> 8 & 0xFF);
    out.write(length >> 16 & 0xFF);


    // Write the packet number
    out.write(request.getPacketNumber());

    request.writeToOutputStream(out);

  }


}
