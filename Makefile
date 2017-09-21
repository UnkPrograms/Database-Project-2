######## start of Makefile #########
# Adapted from Dr. Tia Newhall, Swarthmore College.

CLASSES = $(wildcard *.java)

.PHONY: all classes clean
.SUFFIXES: .java .class
.java.class:
	javac $(JFLAGS) $*.java
all: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class

####### end of Makefile #######