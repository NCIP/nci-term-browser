<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
  <head>
    <title>Term Type Help Informaton</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  </head>

  <body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
    <f:view>
      <%
        Vector abbr_vec = new Vector();
        Vector def_vec = new Vector();
        abbr_vec.add("AB");
        abbr_vec.add("AD");
        abbr_vec.add("AQ<sup>*</sup>");
        abbr_vec.add("AQS");
        abbr_vec.add("BR");
        abbr_vec.add("CA2");
        abbr_vec.add("CA3");
        abbr_vec.add("CNU");
        abbr_vec.add("CI");
        abbr_vec.add("CN");
        abbr_vec.add("CS");
        abbr_vec.add("DN");
        abbr_vec.add("FB");
        abbr_vec.add("HD<sup>*</sup>");
        abbr_vec.add("PT<sup>*</sup>");
        abbr_vec.add("SN");
        abbr_vec.add("SY");

        def_vec.add("Abbreviation");
        def_vec.add("Adjectival form (and other parts of grammer");
        def_vec.add("Antiquated preferred term");
        def_vec.add("Antiguated term, use when there is morem than one antiquated term within a concept");
        def_vec.add("US brand name");
        def_vec.add("ISO 3166 alpha-2 country code");
        def_vec.add("ISO 3166 alpha-3 country code");
        def_vec.add("ISO 3166 numeric country code");
        def_vec.add("IO coutry code (deprecated)");
        def_vec.add("Drug study code");
        def_vec.add("US State Department country code");
        def_vec.add("Display name");
        def_vec.add("Foreign brand name");
        def_vec.add("Header");
        def_vec.add("Preferred term");
        def_vec.add("Chemical structure name");
        def_vec.add("Synonym");
      %>

      <%@ include file="/pages/templates/header.xhtml" %>
      </br/>
      <div class="center-page">
        <div>
          <table width="100%">
            <tr class="standardText3">
              <td align="left">
                <b>Term Type Help Information</b>
              </td>
              <td align="right">
                <font size="1" color="red" align="right">
                  <a href="javascript:printPage()"><img src="<%= request.getContextPath() %>/images/printer.bmp" border="0"><i>Send to Printer</i></a>
                </font>
              </td>
            </tr>
          </table>
          <hr/>
        </div>

        <div>
          <table width="100%">
            <tr class="standardText"><td align="left">
              NCI Thesaurus Term Types use 2- or 3-character abbreviations to code the nature of each term associated with a concept. Here is a listing of the term type codes and their meanings:
            </td></tr>
          </table>
        </div>
        </br>

        <div>
          <table cellpadding="3" cellspacing="0" border="0" class="dataTable" width="100%">
            <%
              for (int n=0; n<abbr_vec.size(); n++) {
              String abbr = (String) abbr_vec.elementAt(n);
              String def = (String) def_vec.elementAt(n);
              String rowColor = (n%2 == 0) ? "dataRowDark" : "dataRowLight";
            %>
              <tr class="<%=rowColor%>">
                <td><%=abbr%></td>
                <td>&nbsp;</td>
                <td><%=def%></td>
              </tr>
            <%
              }
            %>
          </table>
        </div>
        </br>

        <div>
          <table width="100%">
            <tr class="standardText"><td align="left">
              *Note on special rules governing NCI PT, HD, and AQ term types: Each concept should have one, and only one, term coded with one of these three values. The NCI Preferred Term is always taken from one of the NCI terms, normally that with a type of PT (Preferred Term). However, in special cases, a concept will not have a PT term, but instead, will have either an HD (Header) term or an AQ term. These tags are considered equivalent to PT by the software. This means that a concept may have only as single NCI PT, or HD, or AQ term. In those cases where multiple antiquated terms are needed for a concept which is itself coded as antiquated, one should be tagged AQ and the rest tagged AQS.
            </td></tr>
          </table>
        </div>

      </div>
    </f:view>
  </body>
</html>
