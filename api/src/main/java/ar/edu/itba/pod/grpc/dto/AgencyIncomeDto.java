package ar.edu.itba.pod.grpc.dto;

import ar.edu.itba.pod.grpc.CsvWritable;

public class AgencyIncomeDto implements CsvWritable, Comparable<AgencyIncomeDto>{
  private String agency;
  private int year;
  private int month;
  private int ydt;

  public AgencyIncomeDto() {
  }

  public AgencyIncomeDto(String agency, int year, int month, int ydt) {
    this.agency = agency;
    this.year = year;
    this.month = month;
    this.ydt = ydt;
  }

  public String getAgency() {
    return agency;
  }

  public int getYear() {
    return year;
  }

  public int getMonth() {
    return month;
  }

  public int getYdt() {
    return ydt;
  }

  @Override
  public String toCsv() {
    return agency + ";" + year + ";" + month + ";" + ydt;
  }

  @Override
  public int compareTo(AgencyIncomeDto o) {
    int agencyComparison = agency.compareTo(o.getAgency());
    if (agencyComparison != 0) {
      return agencyComparison;
    }
    int yearComparison = Integer.compare(o.getYear(), year);
    if (yearComparison != 0) {
      return yearComparison;
    }
    int monthComparison = Integer.compare(o.getMonth(), month);
    if (monthComparison != 0) {
      return monthComparison;
    }
    return Integer.compare(o.getYdt(), ydt);
  }
}
