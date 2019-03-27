using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;

namespace startWithPrioritet
{
    class Program
    {

        protected static ProcessPriorityClass getPriority(string str)
        {
            ProcessPriorityClass prior = ProcessPriorityClass.Normal;
            // 1 - Idle (низкий)
            // 2 - BellowNormal (ниже среднего)
            // 3 - Normal (средний)
            // 4 - AboveNormal (выше среднего)
            // 5 - high (высокий)
            // 6 - RealTime (реального времени)
            if (str == "1") {
                prior = ProcessPriorityClass.Idle;
            }
            else if (str == "2") {
                prior = ProcessPriorityClass.BelowNormal;

            }
            else if (str == "3") {
                prior = ProcessPriorityClass.Normal;
            }
            else if (str == "4") {
                prior = ProcessPriorityClass.AboveNormal;
            }
            else if (str == "5") {
                prior = ProcessPriorityClass.High;
            }
            else if (str == "6") {
                prior = ProcessPriorityClass.RealTime;
            }

            
            return prior;

        }

        /*
         args[0] - start process
         args[1] - argument
         args[2] - от 1 до 6
         */
        static void Main(string[] args) {
            Process proc = new Process();
            proc.StartInfo.WindowStyle = ProcessWindowStyle.Minimized;

            proc.StartInfo.FileName = args[0].ToString();
            proc.StartInfo.Arguments = args[1].ToString();
            
            proc.Start();
            proc.PriorityClass = getPriority(args[2].ToString());
        }
    }
}
