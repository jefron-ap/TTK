/*
 * Copyright 2011 International Health Terminology Standards Development Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ihtsdo.ttk.helpers.uuid;
/**
 * Copyright (c) 2009 International Health Terminology Standards Development
 * Organisation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/*
 * Copyright 2007 NeHTA in association with Informatics, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class Type5UuidFactory {

    public static UUID PATH_ID_FROM_FS_DESC = UUID.fromString("5a2e7786-3e41-11dc-8314-0800200c9a66");
    public static UUID REL_GROUP_NAMESPACE = UUID.fromString("8972fef0-ad53-11df-94e2-0800200c9a66");
    public static UUID USER_FULLNAME_NAMESPACE = UUID.fromString("cad85220-1ed4-11e1-8bc2-0800200c9a66");
    
    public static UUID AUTHOR_TIME_ID = UUID.fromString("c6915290-30fc-11e1-b86c-0800200c9a66");

    public static final String encoding = "8859_1";

    public static UUID get(UUID namespace, String name) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest sha1Algorithm = MessageDigest.getInstance("SHA-1");

        // Generate the digest.
        sha1Algorithm.reset();
        if (namespace != null) {
            sha1Algorithm.update(getRawBytes(namespace));
        }
        sha1Algorithm.update(name.getBytes(encoding));
        byte[] sha1digest = sha1Algorithm.digest();

        sha1digest[6] &= 0x0f; /* clear version */
        sha1digest[6] |= 0x50; /* set to version 5 */
        sha1digest[8] &= 0x3f; /* clear variant */
        sha1digest[8] |= 0x80; /* set to IETF variant */

        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (sha1digest[i] & 0xff);
        }
        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (sha1digest[i] & 0xff);
        }

        return new UUID(msb, lsb);
    }

    public static UUID get(String name) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return get(null, name);
    }

    /**
     * This routine adapted from org.safehaus.uuid.UUID,
     * which is licensed under Apache 2.
     * 
     * @param uid
     * @return
     */
    public static byte[] getRawBytes(UUID uid) {
        String id = uid.toString();
        if (id.length() != 36) {
            throw new NumberFormatException("UUID has to be represented by the standard 36-char representation");
        }
        byte[] rawBytes = new byte[16];

        for (int i = 0, j = 0; i < 36; ++j) {
            // Need to bypass hyphens:
            switch (i) {
            case 8:
            case 13:
            case 18:
            case 23:
                if (id.charAt(i) != '-') {
                    throw new NumberFormatException("UUID has to be represented by the standard 36-char representation");
                }
                ++i;
            }
            char c = id.charAt(i);

            if (c >= '0' && c <= '9') {
                rawBytes[j] = (byte) ((c - '0') << 4);
            } else if (c >= 'a' && c <= 'f') {
                rawBytes[j] = (byte) ((c - 'a' + 10) << 4);
            } else if (c >= 'A' && c <= 'F') {
                rawBytes[j] = (byte) ((c - 'A' + 10) << 4);
            } else {
                throw new NumberFormatException("Non-hex character '" + c + "'");
            }

            c = id.charAt(++i);

            if (c >= '0' && c <= '9') {
                rawBytes[j] |= (byte) (c - '0');
            } else if (c >= 'a' && c <= 'f') {
                rawBytes[j] |= (byte) (c - 'a' + 10);
            } else if (c >= 'A' && c <= 'F') {
                rawBytes[j] |= (byte) (c - 'A' + 10);
            } else {
                throw new NumberFormatException("Non-hex character '" + c + "'");
            }
            ++i;
        }
        return rawBytes;
    }

}
