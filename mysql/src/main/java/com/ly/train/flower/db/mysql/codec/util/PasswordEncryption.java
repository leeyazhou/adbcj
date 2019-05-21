/*
	This file is part of asyncdb.

	asyncdb is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	asyncdb is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with asyncdb.  If not, see <http://www.gnu.org/licenses/>.

	Copyright 2008  Mike Heath
*/
package com.ly.train.flower.db.mysql.codec.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import com.ly.train.flower.db.api.DbException;


public class PasswordEncryption {

    public static byte[] encryptPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            byte[] hash1 = md.digest(password.getBytes(StandardCharsets.UTF_8));

            md.reset();
            byte[] hash2 = md.digest(hash1);

            md.reset();
            md.update(salt);
            md.update(hash2);

            byte[] digest = md.digest();
            for (int i = 0; i < digest.length; i++) {
                digest[i] = (byte) (digest[i] ^ hash1[i]);
            }
            return digest;
        } catch (Exception e) {
            throw DbException.wrap(e);
        }
    }

}
