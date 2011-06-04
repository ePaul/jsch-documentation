/* -*-mode:java; c-basic-offset:2; -*- */
import java.io.*;
import com.jcraft.jzlib.*;

// Test deflate() with small buffers

class test_deflate_inflate{

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

    ZStream c_stream=new ZStream();

    err=c_stream.deflateInit(JZlib.Z_DEFAULT_COMPRESSION);
    CHECK_ERR(c_stream, err, "deflateInit");

    c_stream.next_in=hello;
    c_stream.next_in_index=0;

    c_stream.next_out=compr;
    c_stream.next_out_index=0;

    while(c_stream.total_in!=hello.length &&
	  c_stream.total_out<comprLen){
      c_stream.avail_in=c_stream.avail_out=1; // force small buffers
      err=c_stream.deflate(JZlib.Z_NO_FLUSH);
      CHECK_ERR(c_stream, err, "deflate");
    }

    while(true){
      c_stream.avail_out=1;
      err=c_stream.deflate(JZlib.Z_FINISH);      
      if(err==JZlib.Z_STREAM_END)break;
      CHECK_ERR(c_stream, err, "deflate");
    }

    err=c_stream.deflateEnd();      
    CHECK_ERR(c_stream, err, "deflateEnd");

    ZStream d_stream=new ZStream();

    d_stream.next_in=compr;
    d_stream.next_in_index=0;
    d_stream.next_out=uncompr;
    d_stream.next_out_index=0;

    err=d_stream.inflateInit();
    CHECK_ERR(d_stream, err, "inflateInit");

    while(d_stream.total_out<uncomprLen &&
      d_stream.total_in<comprLen) {
      d_stream.avail_in=d_stream.avail_out=1; /* force small buffers */
      err=d_stream.inflate(JZlib.Z_NO_FLUSH);
      if(err==JZlib.Z_STREAM_END) break;
      CHECK_ERR(d_stream, err, "inflate");
    }

    err=d_stream.inflateEnd();
    CHECK_ERR(d_stream, err, "inflateEnd");

    int i=0;
    for(;i<hello.length; i++) if(hello[i]==0) break;
    int j=0;
    for(;j<uncompr.length; j++) if(uncompr[j]==0) break;

    if(i==j){
      for(i=0; i<j; i++) if(hello[i]!=uncompr[i]) break;
      if(i==j){
	System.out.println("inflate(): "+new String(uncompr, 0, j));
	return;
      }
    }
    else{
      System.out.println("bad inflate");
    }
  }

  static void CHECK_ERR(ZStream z, int err, String msg) {
    if(err!=JZlib.Z_OK){
      if(z.msg!=null) System.out.print(z.msg+" "); 
      System.out.println(msg+" error: "+err); 
      System.exit(1);
    }
  }
}
