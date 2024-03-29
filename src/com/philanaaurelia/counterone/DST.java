package com.philanaaurelia.counterone;

public class DST {
    //Remember that dates are represented 0 -11
    private static int BEGIN_MONTH = 2;
    private static int BEGIN_DAY;
    private static int BEGIN_HOUR = 2;
    private static int END_HOUR = 1;
    private static int END_DAY;
    private static int END_MONTH = 10;

    public DST(int begin_yr, int end_yr){
        FindBeginningDay(begin_yr);
        FindEndDay(end_yr);

    }
    private void FindBeginningDay(int begin_yr){
        switch(begin_yr){
        case 2010: BEGIN_DAY = 14;
        break;
        case 2011: BEGIN_DAY = 13;
        break;
        case 2012: BEGIN_DAY = 11;
        break;
        case 2013: BEGIN_DAY = 10;
        break;
        case 2014: BEGIN_DAY = 9;
        break;
        case 2015: BEGIN_DAY = 8;
        break;
        case 2016: BEGIN_DAY = 13;
        break;
        case 2017: BEGIN_DAY = 12;
        break;
        case 2018: BEGIN_DAY = 11;
        break;
        case 2019: BEGIN_DAY = 10;
        break;
        case 2020: BEGIN_DAY = 8;
        break;
        case 2021: BEGIN_DAY = 14;
        break;
        case 2022: BEGIN_DAY = 13;
        break;
        case 2023: BEGIN_DAY = 12;
        break;
        case 2024: BEGIN_DAY = 10;
        break;
        case 2025: BEGIN_DAY = 9;
        break;
        case 2026: BEGIN_DAY = 8;
        break;
        case 2027: BEGIN_DAY = 14;
        break;
        case 2028: BEGIN_DAY = 12;
        break;
        case 2029: BEGIN_DAY = 11;
        break;
        case 2030: BEGIN_DAY = 10;
        break;

        }
    }
    private void FindEndDay(int end_yr){
        switch(end_yr){
        case 2010: END_DAY = 7;
        break;
        case 2011: END_DAY = 6;
        break;
        case 2012: END_DAY = 4;
        break;
        case 2013: END_DAY = 3;
        break;
        case 2014: END_DAY = 2;
        break;
        case 2015: END_DAY = 1;
        break;
        case 2016: END_DAY = 6;
        break;
        case 2017: END_DAY = 5;
        break;
        case 2018: END_DAY = 4;
        break;
        case 2019: END_DAY = 3;
        break;
        case 2020: END_DAY = 1;
        break;
        case 2021: END_DAY = 7;
        break;
        case 2022: END_DAY = 6;
        break;
        case 2023: END_DAY = 5;
        break;
        case 2024: END_DAY = 3;
        break;
        case 2025: END_DAY = 2;
        break;
        case 2026: END_DAY = 1;
        break;
        case 2027: END_DAY = 7;
        break;
        case 2028: END_DAY = 5;
        break;
        case 2029: END_DAY = 4;
        break;
        case 2030: END_DAY = 3;
        break;
        }

    }


    public int InDST(int mth, int hr, int day){
        if(mth < BEGIN_MONTH)
            return -1;
        else if(mth == BEGIN_MONTH)
            if(day < BEGIN_DAY)
                return -1;
            else if(day == BEGIN_DAY)
                if(hr < BEGIN_HOUR)
                    return -1;

        if(mth < END_MONTH)
            return 0;
        else if(mth==END_MONTH)
            if(day < END_DAY)
                return 0;
            else if(day == END_DAY)
                if(hr < END_HOUR)
                    return 0;

        return 1;
    }
}
