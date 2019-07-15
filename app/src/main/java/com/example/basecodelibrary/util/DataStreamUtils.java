package com.example.basecodelibrary.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ProtocolException;

/**
 * Utility methods for interacting with {@link DataInputStream} and
 * {@link DataOutputStream}, mostly dealing with writing partial arrays.
 */
public class DataStreamUtils {
    @Deprecated
    public static long[] readFullLongArray(DataInputStream in) throws IOException {
        final int size = in.readInt();
        if (size < 0) throw new ProtocolException("negative array size");
        final long[] values = new long[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = in.readLong();
        }
        return values;
    }

    /**
     * Read variable-length {@link Long} using protobuf-style approach.
     */
    public static int readVarInt(DataInputStream in) throws IOException {
        int shift = 0;
        int result = 0;
        while (shift < 32) {
            byte b = in.readByte();
            result |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0)
                return result;
            shift += 7;
        }
        throw new ProtocolException("malformed long");
    }
    
    /**
     * Write variable-length {@link Long} using protobuf-style approach.
     */
    public static void writeVarInt(DataOutputStream out, int value) throws IOException {
        while (true) {
            if ((value & ~0x7FL) == 0) {
                out.writeByte((int) value);
                return;
            } else {
                out.writeByte(((int) value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }
    }
    
    /**
     * Read variable-length {@link Long} using protobuf-style approach.
     */
    public static long readVarLong(DataInputStream in) throws IOException {
        int shift = 0;
        long result = 0;
        while (shift < 64) {
            byte b = in.readByte();
            result |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0)
                return result;
            shift += 7;
        }
        throw new ProtocolException("malformed long");
    }

    /**
     * Write variable-length {@link Long} using protobuf-style approach.
     */
    public static void writeVarLong(DataOutputStream out, long value) throws IOException {
        while (true) {
            if ((value & ~0x7FL) == 0) {
                out.writeByte((int) value);
                return;
            } else {
                out.writeByte(((int) value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }
    }

    public static long[] readVarLongArray(DataInputStream in) throws IOException {
        final int size = in.readInt();
        if (size == -1) return null;
        if (size < 0) throw new ProtocolException("negative array size");
        final long[] values = new long[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = readVarLong(in);
        }
        return values;
    }

    public static void writeVarLongArray(DataOutputStream out, long[] values, int size)
            throws IOException {
        if (values == null) {
            out.writeInt(-1);
            return;
        }
        if (size > values.length) {
            throw new IllegalArgumentException("size larger than length");
        }
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            writeVarLong(out, values[i]);
        }
    }
    
    public static int[] readVarIntArray(DataInputStream in) throws IOException {
        final int size = in.readInt();
        if (size == -1) return null;
        if (size < 0) throw new ProtocolException("negative array size");
        final int[] values = new int[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = readVarInt(in);
        }
        return values;
    }

    public static void writeVarIntArray(DataOutputStream out, int[] values, int size)
            throws IOException {
        if (values == null) {
            out.writeInt(-1);
            return;
        }
        if (size > values.length) {
            throw new IllegalArgumentException("size larger than length");
        }
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            writeVarInt(out, values[i]);
        }
    }
    
    public static String[] readVarStringArray(DataInputStream in) throws IOException {
        final int size = in.readInt();
        if (size == -1) return null;
        if (size < 0) throw new ProtocolException("negative array size");
        final String[] values = new String[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = in.readUTF();
        }
        return values;
    }
    
    public static void WriteVarStringArray(DataOutputStream out, String [] values) throws IOException{
    	WriteVarStringArray(out, values, values != null ? values.length : 0);
    }
    
    public static void WriteVarStringArray(DataOutputStream out, String [] values, int size) throws IOException{
        if (values == null) {
            out.writeInt(-1);
            return;
        }
        if (size > values.length) {
            throw new IllegalArgumentException("size larger than length");
        }
        out.writeInt(size);
        String strval = null ;
        for (int i = 0; i < size; i++) {
        	strval = values[i];
        	if (strval == null){
        		strval = "";
        	}
            out.writeUTF(strval);
        }
    }
}
