#!/usr/local/bin/perl
#
# From http://accad.osu.edu/~mlewis/Class/Perl/perl.html#fiad
#
# search for a file in all subdirectories
#
if ($#ARGV != 0) {
    print "usage: findfile filename\n";
    exit;
}

$filename = $ARGV[0];

# look in current directory
$dir = `pwd`;
chop($dir);
&searchDirectory($dir);

sub searchDirectory {
    local($dir);
    local(@lines);
    local($line);
    local($file);
    local($subdir);

    $dir = $_[0];

    # check for permission
    if(-x $dir) {

	# search this directory
	@lines = `cd $dir; ls -l | grep $filename`;
	foreach $line (@lines) {
	    $line =~ /\s+(\S+)$/;
	    $file = $1;
	    print "Found $file in $dir\n";
	}
	
	# search any sub directories
	@lines = `cd $dir; ls -l`;
	foreach $line (@lines) {
	    if($line =~ /^d/) {
		$line =~ /\s+(\S+)$/;
		$subdir = $dir."/".$1;
		&searchDirectory($subdir);
	    }
	}
    }
}
