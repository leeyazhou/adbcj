/**
 * Copyright © 2019 yazhou.li (lee_yazhou@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ly.train.flower.db.mysql.codec;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class BoundedInputStream extends InputStream {

  private final InputStream in;
  private int remaining;
  // Support mark()/reset 2017-09-01 little-pan
  private int position, mark = -1;

  public BoundedInputStream(InputStream in, int length) {
    this.in = in;
    this.remaining = length;
  }

  @Override
  public int read() throws IOException {
    int i = in.read();
    if (i >= 0) {
      remaining--;
      position++;
    }
    if (remaining < 0) {
      throw new IllegalStateException("Buffer overrun");
    }
    return i;
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    int i = in.read(b, off, len);
    remaining -= i;
    position += i;
    if (remaining < 0) {
      throw new IllegalStateException("Read too many bytes");
    }
    return i;
  }

  @Override
  public long skip(long n) throws IOException {
    long i = in.skip(n);
    remaining -= i;
    position += i;
    if (remaining < 0) {
      throw new IllegalStateException("Read too many bytes");
    }
    return i;
  }

  public int getRemaining() {
    return remaining;
  }

  public void readFully(byte[] buffer) throws IOException {
    readFully(buffer, 0, buffer.length);
  }

  public void readFully(byte buffer[], int off, int length) throws IOException {
    if (length < 0)
      throw new IndexOutOfBoundsException();
    int count = 0;
    while (count < length) {
      int read = in.read(buffer, off + count, length - count);
      remaining -= read;
      position += read;
      if (read < 0)
        throw new EOFException("Expected to read " + length + ". But stream ended at " + count);
      count += read;
    }
  }

  /**
   * @since 2017-09-01 little-pan
   */
  @Override
  public void mark(final int readlimit) {
    in.mark(readlimit);
    mark = position;
  }

  /**
   * @since 2017-09-01 little-pan
   */
  @Override
  public void reset() throws IOException {
    in.reset();
    remaining += (position - mark);
    position = mark;
    mark = -1;
  }

  /**
   * @since 2017-09-01 little-pan
   */
  @Override
  public boolean markSupported() {
    return in.markSupported();
  }

}
