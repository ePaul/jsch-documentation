/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/*
Copyright (c) 2002-2011 ymnk, JCraft,Inc. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice,
     this list of conditions and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright 
     notice, this list of conditions and the following disclaimer in 
     the documentation and/or other materials provided with the distribution.

  3. The names of the authors may not be used to endorse or promote products
     derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.jcraft.jsch;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Attributes of a (remote) file manipulated via Sftp.
 * An SftpATTRS object can contain a variable number of actual
 * attributes. A {@code flags} field defines which attributes
 * are present in the structure, and then only these follow.
 *<p>
 *  This class manages the flags automatically, the setXXX methods
 *  also set the corresponding flag.
 *</p>
 *<p>
 *  When changing attributes using
 *   {@link ChannelSftp#setStat ChannelSftp.setStat()}, only these
 *  attributes actually contained in the structure are sent to the
 *  server and will be changed.
 *</p>
 *<p>
 * This class corresponds to the ATTRS structure in the form defined
 * in
 * <a href="http://tools.ietf.org/html/draft-ietf-secsh-filexfer-02#section-5">
 *  version 00-02 of the Internet draft <em>SSH File Transfer Protocol</em></a>,
 *  corresponding to version 3 of the SSH File transfer protocol.
 * (Later versions changed the format, and there is no version actually
 *  published as RFC.)
 *</p>
 * Here is a quote from the specification:
 * <pre>
 *   uint32   flags
 *   uint64   size           present only if flag SSH_FILEXFER_ATTR_SIZE
 *   uint32   uid            present only if flag SSH_FILEXFER_ATTR_UIDGID
 *   uint32   gid            present only if flag SSH_FILEXFER_ATTR_UIDGID
 *   uint32   permissions    present only if flag SSH_FILEXFER_ATTR_PERMISSIONS
 *   uint32   atime          present only if flag SSH_FILEXFER_ACMODTIME
 *   uint32   mtime          present only if flag SSH_FILEXFER_ACMODTIME
 *   uint32   extended_count present only if flag SSH_FILEXFER_ATTR_EXTENDED
 *   string   extended_type
 *   string   extended_data
 *     ...      more extended data (extended_type - extended_data pairs),
 *              so that number of pairs equals extended_count
 * </pre>
 * @see ChannelSftp
 * @see ChannelSftp#stat stat()
 * @see ChannelSftp#lstat lstat()
 * @see ChannelSftp#setStat setStat()
 */
public class SftpATTRS {

  static final int S_ISUID = 04000; // set user ID on execution
  static final int S_ISGID = 02000; // set group ID on execution
  static final int S_ISVTX = 01000; // sticky bit   ****** NOT DOCUMENTED *****

  static final int S_IRUSR = 00400; // read by owner
  static final int S_IWUSR = 00200; // write by owner
  static final int S_IXUSR = 00100; // execute/search by owner
  static final int S_IREAD = 00400; // read by owner
  static final int S_IWRITE= 00200; // write by owner
  static final int S_IEXEC = 00100; // execute/search by owner

  static final int S_IRGRP = 00040; // read by group
  static final int S_IWGRP = 00020; // write by group
  static final int S_IXGRP = 00010; // execute/search by group

  static final int S_IROTH = 00004; // read by others
  static final int S_IWOTH = 00002; // write by others
  static final int S_IXOTH = 00001; // execute/search by others

  /**
   * The bitmask containing all the bits defined above,
   * but not the link or directory bits.
   */
  private static final int pmask = 0xFFF;


  /**
   * Returns a string representation of the permissions
   * in the format used by {@code ls -l}.
   */
  public String getPermissionsString() {
    StringBuffer buf = new StringBuffer(10);

    if(isDir()) buf.append('d');
    else if(isLink()) buf.append('l');
    else buf.append('-');

    if((permissions & S_IRUSR)!=0) buf.append('r');
    else buf.append('-');

    if((permissions & S_IWUSR)!=0) buf.append('w');
    else buf.append('-');

    if((permissions & S_ISUID)!=0) buf.append('s');
    else if ((permissions & S_IXUSR)!=0) buf.append('x');
    else buf.append('-');

    if((permissions & S_IRGRP)!=0) buf.append('r');
    else buf.append('-');

    if((permissions & S_IWGRP)!=0) buf.append('w');
    else buf.append('-');

    if((permissions & S_ISGID)!=0) buf.append('s');
    else if((permissions & S_IXGRP)!=0) buf.append('x');
    else buf.append('-');

    if((permissions & S_IROTH) != 0) buf.append('r');
    else buf.append('-');

    if((permissions & S_IWOTH) != 0) buf.append('w');
    else buf.append('-');

    if((permissions & S_IXOTH) != 0) buf.append('x');
    else buf.append('-');
    return (buf.toString());
  }

  /**
   * returns a string representation of the access time.
   */
  public String  getAtimeString(){
    SimpleDateFormat locale=new SimpleDateFormat();
    // shouldn't this be  new Date(1000L*atime)   ? -- P.E.
    return (locale.format(new Date(atime)));
  }

  /**
   * returns a string representation of the modifiying time.
   */
  public String  getMtimeString(){
    Date date= new Date(((long)mtime)*1000);
    return (date.toString());
  }

  /**
   * Flag indicating the presence of the {@link #getSize size}
   * attribute.
   */
  public static final int SSH_FILEXFER_ATTR_SIZE=         0x00000001;
  /**
   * Flag indicating the presence of the {@link #getUId uid}
   * and {@link #getGId gid} attributes.
   */
  public static final int SSH_FILEXFER_ATTR_UIDGID=       0x00000002;
  /**
   * Flag indicating the presence of the {@link #getPermissions permissions}
   * attribute.
   */
  public static final int SSH_FILEXFER_ATTR_PERMISSIONS=  0x00000004;
  /**
   * Flag indicating the presence of the {@link #getATime atime}
   * and {@link #getMTime mtime} attributes.
   */
  public static final int SSH_FILEXFER_ATTR_ACMODTIME=    0x00000008;
  /**
   * Flag indicating the presence of
   * {@linkplain #getExtended extended attributes}.
   */
  public static final int SSH_FILEXFER_ATTR_EXTENDED=     0x80000000;

  static final int S_IFDIR=0x4000;
  static final int S_IFLNK=0xa000;

  int flags=0;
  long size;
  int uid;
  int gid;
  int permissions;
  int atime;
  int mtime;
  String[] extended=null;

  private SftpATTRS(){
  }

  /**
   * parses an ATTR structure from a buffer.
   */
  static SftpATTRS getATTR(Buffer buf){
    SftpATTRS attr=new SftpATTRS();	
    attr.flags=buf.getInt();
    if((attr.flags&SSH_FILEXFER_ATTR_SIZE)!=0){ attr.size=buf.getLong(); }
    if((attr.flags&SSH_FILEXFER_ATTR_UIDGID)!=0){
      attr.uid=buf.getInt(); attr.gid=buf.getInt();
    }
    if((attr.flags&SSH_FILEXFER_ATTR_PERMISSIONS)!=0){ 
      attr.permissions=buf.getInt();
    }
    if((attr.flags&SSH_FILEXFER_ATTR_ACMODTIME)!=0){ 
      attr.atime=buf.getInt();
    }
    if((attr.flags&SSH_FILEXFER_ATTR_ACMODTIME)!=0){ 
      attr.mtime=buf.getInt(); 
    }
    if((attr.flags&SSH_FILEXFER_ATTR_EXTENDED)!=0){
      int count=buf.getInt();
      if(count>0){
	attr.extended=new String[count*2];
	for(int i=0; i<count; i++){
	  attr.extended[i*2]=Util.byte2str(buf.getString());
	  attr.extended[i*2+1]=Util.byte2str(buf.getString());
	}
      }
    }
    return attr;
  } 

  /**
   * returns the length of the ATTR structure this object
   * would be serialized to.
   */
  int length(){
    int len=4;

    if((flags&SSH_FILEXFER_ATTR_SIZE)!=0){ len+=8; }
    if((flags&SSH_FILEXFER_ATTR_UIDGID)!=0){ len+=8; }
    if((flags&SSH_FILEXFER_ATTR_PERMISSIONS)!=0){ len+=4; }
    if((flags&SSH_FILEXFER_ATTR_ACMODTIME)!=0){ len+=8; }
    if((flags&SSH_FILEXFER_ATTR_EXTENDED)!=0){
      len+=4;
      int count=extended.length/2;
      if(count>0){
	for(int i=0; i<count; i++){
	  len+=4; len+=extended[i*2].length();
	  len+=4; len+=extended[i*2+1].length();
	}
      }
    }
    return len;
  }

  /**
   * writes the ATTR structure to a buffer.
   */
  void dump(Buffer buf){
    buf.putInt(flags);
    if((flags&SSH_FILEXFER_ATTR_SIZE)!=0){ buf.putLong(size); }
    if((flags&SSH_FILEXFER_ATTR_UIDGID)!=0){
      buf.putInt(uid); buf.putInt(gid);
    }
    if((flags&SSH_FILEXFER_ATTR_PERMISSIONS)!=0){ 
      buf.putInt(permissions);
    }
    if((flags&SSH_FILEXFER_ATTR_ACMODTIME)!=0){ buf.putInt(atime); }
    if((flags&SSH_FILEXFER_ATTR_ACMODTIME)!=0){ buf.putInt(mtime); }
    if((flags&SSH_FILEXFER_ATTR_EXTENDED)!=0){
      int count=extended.length/2;
      // where is the buf.putInt(count)?  -- P.E.
      if(count>0){
	for(int i=0; i<count; i++){
	  buf.putString(Util.str2byte(extended[i*2]));
	  buf.putString(Util.str2byte(extended[i*2+1]));
	}
      }
    }
  }

  /**
   * sets the flags indicating which fields are included.
   */
  void setFLAGS(int flags){
    this.flags=flags;
  }

  /**
   * sets the size.
   */
  public void setSIZE(long size){
    flags|=SSH_FILEXFER_ATTR_SIZE;
    this.size=size;
  }

  /**
   * Sets user and group Identifier.
   */
  public void setUIDGID(int uid, int gid){
    flags|=SSH_FILEXFER_ATTR_UIDGID;
    this.uid=uid;
    this.gid=gid;
  }

  /**
   * Sets access and modification time.
   */
  public void setACMODTIME(int atime, int mtime){
    flags|=SSH_FILEXFER_ATTR_ACMODTIME;
    this.atime=atime;
    this.mtime=mtime;
  }

  /**
   * sets the file permissions.
   * @param permissions a bit mask containing some combination
   *  of the bits 0-11.
   */
  public void setPERMISSIONS(int permissions){
    flags|=SSH_FILEXFER_ATTR_PERMISSIONS;
    permissions=(this.permissions&~pmask)|(permissions&pmask);
    this.permissions=permissions;
  }

  /**
   * checks whether this file is a directory.
   * @return true if the permissions are included in the
   *    structure and the directory bit is set.
   */
  public boolean isDir(){
    return ((flags&SSH_FILEXFER_ATTR_PERMISSIONS)!=0 && 
	    ((permissions&S_IFDIR)==S_IFDIR));
  }

  /**
   * checks whether this file is a symbolic link.
   * @return true if the permissions are included in the
   *    structure and the link bits are set.
   */
  public boolean isLink(){
    return ((flags&SSH_FILEXFER_ATTR_PERMISSIONS)!=0 && 
	    ((permissions&S_IFLNK)==S_IFLNK));
  }

  /**
   * returns the flags indicating which attributes
   * are present.
   */
  public int getFlags() { return flags; }

  /**
   * Returns the size of the file, in bytes.
   */
  public long getSize() { return size; }

  /**
   * returns the numerical user identifier of the owning user.
   * <blockquote>
   *   The `uid' and `gid' fields contain numeric Unix-like user and group
   *   identifiers, respectively.
   * </blockquote>
   */
  public int getUId() { return uid; }

  /**
   * returns the numerical group identifier of the owning group.
   * <blockquote>
   *   The `uid' and `gid' fields contain numeric Unix-like user and group
   *   identifiers, respectively.
   * </blockquote>
   */
  public int getGId() { return gid; }

  /**
   * Returns the Unix permissions of the file.
   * <blockquote>
   *   The `permissions' field contains a bit mask of file permissions as
   *   defined by posix.
   * </blockquote>
   */
  public int getPermissions() { return permissions; }

  /**
   * returns the last access time.
   * <blockquote>
   *    The `atime' and `mtime' contain the access and modification times of
   *    the files, respectively.  They are represented as seconds from Jan 1,
   *    1970 in UTC.
   * </blockquote>
   */
  public int getATime() { return atime; }

  /**
   * returns the last modification time.
   * <blockquote>
   *    The `atime' and `mtime' contain the access and modification times of
   *    the files, respectively.  They are represented as seconds from Jan 1,
   *    1970 in UTC.
   * </blockquote>
   */
  public int getMTime() { return mtime; }
  /**
   * returns extended attributes, if any.
   * @return the attributes, in the form of a string array, alternating
   *   type identifier and value.
   */
  public String[] getExtended() { return extended; }

  /**
   * creates a string representation of this object.
   *  This string contains permissions, UID, GID, size and modification time.
   */
  public String toString() {
    return (getPermissionsString()+" "+getUId()+" "+getGId()+" "+getSize()+" "+getMtimeString());
  }
  /*
  public String toString(){
    return (((flags&SSH_FILEXFER_ATTR_SIZE)!=0) ? ("size:"+size+" ") : "")+
           (((flags&SSH_FILEXFER_ATTR_UIDGID)!=0) ? ("uid:"+uid+",gid:"+gid+" ") : "")+
           (((flags&SSH_FILEXFER_ATTR_PERMISSIONS)!=0) ? ("permissions:0x"+Integer.toHexString(permissions)+" ") : "")+
           (((flags&SSH_FILEXFER_ATTR_ACMODTIME)!=0) ? ("atime:"+atime+",mtime:"+mtime+" ") : "")+
           (((flags&SSH_FILEXFER_ATTR_EXTENDED)!=0) ? ("extended:?"+" ") : "");
  }
  */
}
