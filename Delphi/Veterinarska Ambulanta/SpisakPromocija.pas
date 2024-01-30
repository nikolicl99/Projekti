unit SpisakPromocija;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, System.Rtti,
  FMX.Grid.Style, FMX.Controls.Presentation, FMX.ScrollBox, FMX.Grid,
  FMX.StdCtrls, FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Param,
  FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf, FireDAC.DApt.Intf,
  FireDAC.Stan.Async, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client, FMX.Objects, FMX.DateTimeCtrls;

type
  TfrmPromocije = class(TForm)
    Promocije: TStringGrid;
    IDPromocije: TStringColumn;
    Odrzanost: TStringColumn;
    VrstaPromocije: TStringColumn;
    Datum: TStringColumn;
    Opis: TStringColumn;
    Dodaj: TButton;
    Odjava: TButton;
    Ukloni: TButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    Datum_Edit: TDateEdit;
    Izmeni: TButton;
    Spisak: TButton;
    Datum_Label: TLabel;
    procedure FormCreate(Sender: TObject);
    procedure OdjavaClick(Sender: TObject);
    procedure DodajClick(Sender: TObject);
    procedure UkloniClick(Sender: TObject);
    procedure PromocijeCellClick(const Column: TColumn; const Row: Integer);
    procedure Datum_EditChange(Sender: TObject);
    procedure PromocijeCellDblClick(const Column: TColumn; const Row: Integer);
    procedure IzmeniClick(Sender: TObject);
    procedure SpisakClick(Sender: TObject);
  private
    { Private declarations }

  public
    { Public declarations }
    SelectedID: integer;
    SelectedDate: string;
    SelectedTip: string;
    SelectedOpis: string;
  end;

var
  frmPromocije: TfrmPromocije;

implementation

{$R *.fmx}
uses Main, PromocijaLista, LoginZaposleni, DodavanjePromocije, BivsaPromocija, Vlasnik_Promocija, PromocijaOpis;

procedure TfrmPromocije.Datum_EditChange(Sender: TObject);
var
  Query: TFDQuery;
  Row: Integer;
begin
  Query := TFDQuery.Create(nil);
  try
    Query.Connection := GlobalConnection;
    Query.SQL.Text :=
      'SELECT promocije.idpromocije, vrste_promocija.naziv, promocije.datum, promocije.opis, ' +
      '  CASE WHEN EXISTS(SELECT 1 FROM zaposleni_promocije WHERE zaposleni_promocije.idpromocije = promocije.idpromocije) ' +
      '        OR EXISTS(SELECT 1 FROM klijenti_promocije WHERE klijenti_promocije.idpromocije = promocije.idpromocije) ' +
      '    THEN ''ODRŽANO'' ELSE ''NIJE ODRŽANO'' END AS status ' +
      'FROM promocije ' +
      'JOIN vrste_promocija ON promocije.vrsta_promocije = vrste_promocija.idpromocije '+
      'WHERE promocije.datum = :Datum';
    Query.ParamByName('Datum').AsString := FormatDateTime('dd/mm/yyyy', Datum_Edit.Date);;
    Query.Open;

    Promocije.RowCount := Query.RecordCount;

    Row := 0;
    while not Query.Eof do
    begin
      Promocije.Cells[0, Row] := Query.FieldByName('idpromocije').AsString;
      Promocije.Cells[1, Row] := Query.FieldByName('naziv').AsString;
      Promocije.Cells[2, Row] := Query.FieldByName('datum').AsString;
      Promocije.Cells[3, Row] := Query.FieldByName('opis').AsString;
      Promocije.Cells[4, Row] := Query.FieldByName('status').AsString;
      Query.Next;
      Inc(Row);
    end;

  finally
    Query.Free;
  end;
end;

procedure TfrmPromocije.DodajClick(Sender: TObject);
begin
frmDodavanjePromocije.show;
self.hide;
end;

procedure TfrmPromocije.FormCreate(Sender: TObject);
var
  Query: TFDQuery;
  Row: Integer;
  Datum: string;
begin
  Query := TFDQuery.Create(nil);
  try
    Datum_Edit.Date := Now;
    Datum := FormatDateTime('dd/mm/yyyy', Now);
    Query.Connection := GlobalConnection;
    Query.SQL.Text :=
      'SELECT promocije.idpromocije, vrste_promocija.naziv, promocije.datum, promocije.opis, ' +
      '  CASE WHEN EXISTS(SELECT 1 FROM zaposleni_promocije WHERE zaposleni_promocije.idpromocije = promocije.idpromocije) ' +
      '        OR EXISTS(SELECT 1 FROM klijenti_promocije WHERE klijenti_promocije.idpromocije = promocije.idpromocije) ' +
      '    THEN ''ODRŽANO'' ELSE ''NIJE ODRŽANO'' END AS status ' +
      'FROM promocije ' +
      'JOIN vrste_promocija ON promocije.vrsta_promocije = vrste_promocija.idpromocije '+
      'WHERE promocije.datum = :Datum';
    Query.ParamByName('Datum').AsString := Datum;
    Query.Open;

    Promocije.RowCount := Query.RecordCount;

    Row := 0;
    while not Query.Eof do
    begin
      Promocije.Cells[0, Row] := Query.FieldByName('idpromocije').AsString;
      Promocije.Cells[1, Row] := Query.FieldByName('naziv').AsString;
      Promocije.Cells[2, Row] := Query.FieldByName('datum').AsString;
      Promocije.Cells[3, Row] := Query.FieldByName('opis').AsString;
      Promocije.Cells[4, Row] := Query.FieldByName('status').AsString;
      Query.Next;
      Inc(Row);
    end;

  finally
    Query.Free;
  end;
end;


procedure TfrmPromocije.IzmeniClick(Sender: TObject);
begin
frmEditPromocija.show;
end;

procedure TfrmPromocije.OdjavaClick(Sender: TObject);
begin
frmLogInZaposleni.show;
self.hide;
end;

procedure TfrmPromocije.PromocijeCellClick(const Column: TColumn;
  const Row: Integer);
begin
        SelectedID:= Promocije.Cells[0, Row].ToInteger;
end;

procedure TfrmPromocije.PromocijeCellDblClick(const Column: TColumn;
  const Row: Integer);
begin
  if Promocije.Row >= 0 then
  begin
    if Promocije.Cells[4, Promocije.Row] = 'ODRŽANO' then
    begin
        SelectedID:= Promocije.Cells[0, Row].ToInteger;
        SelectedTip:= Promocije.Cells[1, Row];
        SelectedDate:= Promocije.Cells[2, Row];
        SelectedOpis:= Promocije.Cells[3, Row];
       frmBivsaPromocija.show;
       self.hide;
    end
    else if Promocije.Cells[4, Promocije.Row] = 'NIJE ODRŽANO' then
    begin
      SelectedID:= Promocije.Cells[0, Row].ToInteger;
        SelectedTip:= Promocije.Cells[1, Row];
        SelectedDate:= Promocije.Cells[2, Row];
        frmVlasnikPromocije.show;
        self.hide;
    end;
  end;
end;

procedure TfrmPromocije.SpisakClick(Sender: TObject);
begin
frmListaPromocija.show;
self.hide;
end;

procedure TfrmPromocije.UkloniClick(Sender: TObject);
var
  Query: TFDQuery;
begin
Query := TFDQuery.Create(nil);
try
Query.Connection:= GlobalConnection;
Query.SQL.Text := 'DELETE FROM Promocije WHERE idpromocije = :promocijaid';
  Query.ParamByName('promocijaid').AsInteger := SelectedID;
  Query.ExecSQL;
finally
  Query.free;
  showmessage('Promocija uklonjena');
  frmpromocije.Activate;
end;
end;
end.
