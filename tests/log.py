import datetime

def startLog():
    f = open("robotLog.txt", "a+")
    f.write("\nTest started at " + datetime.datetime.now().strftime("%d/%m/%Y  %H:%M:%S") + "\n")
    f.close()

def stopLog():
    f = open("robotLog.txt", "a+")
    f.write("Test ended at " + datetime.datetime.now().strftime("%d/%m/%Y  %H:%M:%S") + "\n")
    f.close()

def logTestCase(test_name, test_status):
    f = open("robotLog.txt", "a+")
    f.write("\t Currently testing " + test_name + " and it got the status " + test_status + "\n")
    f.close()