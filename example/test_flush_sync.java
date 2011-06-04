/* -*-mode:java; c-basic-offset:2; -*- */
import java.io.*;
import com.jcraft.jzlib.*;

// Test deflate() with full flush
class test_flush_sync{

  static final byte[] hello="hello, hello! ".getBytes();
  static{
    hello[hello.length-1]=0;
  }

  public static void main(String[] arg){
    int err;
    int comprLen=40000;
    int uncomprLen=comprLen;
    byte[] compr=new byte[comprLen];
    byte[] uncompr=new byte[uncomprLen];
    int len = hello.length;

    ZStream c_stream=new ZStream();

    err=c_stream.deflateInit(JZlib.Z_DEFAULT_COMPRESSION);
    CHECK_ERR(c_stream, err, "deflate");

    c_stream.next_in =hello;
    c_stream.next_in_index =0;
    c_stream.next_out = compr;
    c_stream.next_out_index = 0;
    c_stream.avail_in = 3;
    c_stream.avail_out = comprLen;

    err = c_stream.deflate(JZlib.Z_FULL_FLUSH);
    CHECK_ERR(c_stream, err, "deflate");

    compr[3]++;              // force an error in first compressed block
    c_stream.avail_in=len-3;

    err = c_stream.deflate(JZlib.Z_FINISH);
    if(err!=JZlib.Z_STREAM_END){
      CHECK_ERR(c_stream, err, "deflate");
    }
    err = c_stream.deflateEnd();
    CHECK_ERR(c_stream, err, "deflateEnd");
    comprLen=(int)(c_stream.total_out);

    ZStream d_stream=new ZStream();

    d_stream.next_in=compr;
    d_stream.next_in_index=0;
    d_stream.avail_in=2;

    err=d_stream.inflateInit();
    CHECK_ERR(d_stream, err, "inflateInit");
    d_stream.next_out=uncompr;
    d_stream.next_out_index=0;
    d_stream.avail_out=uncomprLen;
    
    err=d_stream.inflate(JZlib.Z_NO_FLUSH);
    CHECK_ERR(d_stream, err, "inflate");

    d_stream.avail_in=comprLen-2;

    err=d_stream.inflateSync();
    CHECK_ERR(d_stream, err, "inflateSync");

    err=d_stream.inflate(JZlib.Z_FINISH);
    if (err != JZlib.Z_DATA_ERROR) {
      System.out.println("inflate should report DATA_ERROR");
        /* Because of incorrect adler32 */
      System.exit(1);
    }

    err=d_stream.inflateEnd();
    CHECK_ERR(d_stream, err, "inflateEnd");

    int j=0;
    for(;j<uncompr.length; j++) if(uncompr[j]==0) break;

    System.out.println("after inflateSync(): hel"+new String(uncompr, 0, j));
  }

  static void CHECK_ERR(ZStream z, int err, String msg) {
    if(err!=JZlib.Z_OK){
      if(z.msg!=null) System.out.print(z.msg+" "); 
      System.out.println(msg+" error: "+err); 

      System.exit(1);
    }
  }
}
