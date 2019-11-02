package cn.io.study3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 切割文件
 * @author dell
 *
 */

public class SplitFile {
	private File src;//指定文件
	private File destDir;
	private int blockSize;//指定大小
	private List<File> destPath;//目标路径
	private int size;//块数
	public SplitFile(String srcPath,String destDir,int blockSize) {
		super();
		this.src = new File(srcPath);
		this.blockSize = blockSize;
		this.destDir=new File(destDir);
		this.destPath=new ArrayList<File>();
		init();
	}
	private void init() {
		int len=(int)this.src.length();//文件长度
		System.out.println(len);
		this.size=(int)Math.ceil((len*1.0)/blockSize);//实际块数
		for(int i=0;i<size;i++) {
			this.destPath.add(new File(this.destDir+"/"+i+this.src.getName()));
		}
		destDir.mkdirs();
	}
	
	public void readSplit() throws IOException {
		int len=(int)this.src.length();//文件长度
		int beginPos=0;
		int actualSize=len<blockSize?len:blockSize;
		for(int i=0;i<size;i++) {
			beginPos=i*blockSize;
			if(i==size-1) {
				actualSize=(int)len;
			}else {
				actualSize=blockSize;
				len-=blockSize;
			}
			System.out.println(i+1+"-->"+beginPos+"-->"+actualSize);
			split(i,beginPos,actualSize);
			
		}
		
	}
	private void split(int i, int beginPos, int actualSize) throws IOException {
		// TODO Auto-generated method stub
		RandomAccessFile rafR=new RandomAccessFile(this.src,"r");
		RandomAccessFile rafW=new RandomAccessFile(this.destPath.get(i),"rw");
		rafR.seek(beginPos);
		//读取
		byte[] flush=new byte[1024];
		int len=-1;
		while((len=rafR.read(flush))!=-1) {
			if(actualSize>len) {
				rafW.write(flush, 0, len);
				actualSize-=len;
			}else {
				rafW.write(flush, 0, actualSize);
			}
		}
		rafR.close();
		rafW.close();
	}
	/**
	 * 文件的合并
	 * @throws IOException 
	 */
	public void merge(String destPath) throws IOException {
		OutputStream os=new BufferedOutputStream(new FileOutputStream(destPath,true));
		Vector<InputStream> vi=new Vector<>();
		SequenceInputStream sis=null;
		for(int i=0;i<size;i++) {
			vi.add(new BufferedInputStream(new FileInputStream(this.destPath.get(i))));
			
		}
		sis=new SequenceInputStream(vi.elements());
		byte[] flush=new byte[1024];
		int len=-1;
		while((len=sis.read(flush))!=-1) {
			os.write(flush,0,len);
		}
		os.flush();
		sis.close();
		os.close();
	}
	public static void main(String[] args) throws IOException {
		SplitFile sp=new SplitFile("aaa.txt","destDir",4);
		sp.readSplit();
		sp.merge("destpath.txt");
	}

}
