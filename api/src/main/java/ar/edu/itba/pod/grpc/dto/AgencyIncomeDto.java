package ar.edu.itba.pod.grpc.dto;

import ar.edu.itba.pod.grpc.CsvWritable;

public class AgencyIncomeDto implements CsvWritable, Comparable<AgencyIncomeDto> {
  private String agency;
  private int year;
  private int month;
  private int ytd;

  public AgencyIncomeDto() {
  }

  public AgencyIncomeDto(String agency, int year, int month, int ytd) {
    this.agency = agency;
    this.year = year;
    this.month = month;
    this.ytd = ytd;
  }

  @Override
  public String toCsv() {
    // Aquí está el problema, debemos asegurarnos de que agency no tenga el year-month
    String cleanAgency = agency.split("-")[0];  // Extraer solo el nombre de la agencia
    return String.format("%s;%d;%d;%d", cleanAgency, year, month, ytd);
  }

  @Override
  public int compareTo(AgencyIncomeDto o) {
    // Asegurarnos de comparar solo el nombre de la agencia
    String thisAgency = this.agency.split("-")[0];
    String otherAgency = o.agency.split("-")[0];

    int agencyComparison = thisAgency.compareTo(otherAgency);
    if (agencyComparison != 0) {
      return agencyComparison;
    }
    int yearComparison = Integer.compare(year, o.year);
    if (yearComparison != 0) {
      return yearComparison;
    }
    return Integer.compare(month, o.month);
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

  public int getYtd() {
    return ytd;
  }


}
