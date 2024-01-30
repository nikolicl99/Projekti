unit SpisakTermina;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, System.Rtti,
  FMX.Grid.Style, FMX.StdCtrls, FMX.Grid, FMX.Controls.Presentation,
  FMX.ScrollBox, FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Param,
  FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf, FireDAC.DApt.Intf,
  FireDAC.Stan.Async, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client, FMX.Objects;

type
  TfrmSpisakTermina = class(TForm)
    Termini: TStringGrid;
    ID: TIntegerColumn;
    Ljubimac: TStringColumn;
    Datum: TDateColumn;
    Vreme: TTimeColumn;
    Nazad: TButton;
    Dodaj: TButton;
    Ukloni: TButton;
    Tip: TStringColumn;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    Vakcinacija: TStringColumn;
    procedure FormCreate(Sender: TObject);
    procedure DodajClick(Sender: TObject);
    procedure NazadClick(Sender: TObject);
    procedure UkloniClick(Sender: TObject);
    procedure TerminCellClick(const Column: TColumn;
  const Row: Integer);
  private
    { Private declarations }
    SelectedID: integer;
  public
    { Public declarations }
  end;

var
  frmSpisakTermina: TfrmSpisakTermina;

implementation

{$R *.fmx}
uses main, loginforma, Zakazivanje_termina, Vlasnik_GlavnaForma;

procedure TfrmSpisakTermina.DodajClick(Sender: TObject);
begin
frmzakazivanjetermina.show;
self.hide;
end;

procedure TfrmSpisakTermina.FormCreate(Sender: TObject);
var
  Query: TFDQuery;
  Row: Integer;
  DanasnjiDatum: string;
begin
  Query := TFDQuery.Create(nil);
  DanasnjiDatum := FormatDateTime('dd/mm/yyyy', Now);
  try
    Query.Connection := GlobalConnection;
    Query.SQL.Text :=
      'SELECT termin.idtermina, termin.vakcinacija, ljubimci.imeljubimca, tipovi_ljubimaca.ime_tipa, termin.datum, termin.vreme ' +
  'FROM termin ' +
  'JOIN ljubimci ON termin.ljubimac = ljubimci.idljubimca ' +
  'JOIN rasa_ljubimca ON ljubimci.rasa = rasa_ljubimca.idrase ' +
  'JOIN tipovi_ljubimaca ON rasa_ljubimca.tipljubimca = tipovi_ljubimaca.idtipa ' +
  'WHERE termin.klijent = :vlasnik '+
  'AND termin.idtermina NOT IN (SELECT termin FROM pregled) '+
  'AND julianday(substr(Termin.Datum, 7, 4) || "-" || substr(Termin.Datum, 4, 2) || "-" || substr(Termin.Datum, 1, 2)) >= julianday(:Danasnji) ';
  Query.ParamByName('vlasnik').AsInteger := frmlogin.UlogovaniVlasnikID;
  Query.ParamByName('Danasnji').AsString := FormatDateTime('yyyy-mm-dd', StrToDate(DanasnjiDatum));
    Query.Open;
    Termini.RowCount := Query.RecordCount;

    Row := 0;
    while not Query.Eof do
    begin
      Termini.Cells[0, Row] := Query.FieldByName('idtermina').AsString;
      Termini.Cells[1, Row] := Query.FieldByName('imeljubimca').AsString;
      Termini.Cells[2, Row] := Query.FieldByName('ime_tipa').AsString;
      Termini.Cells[3, Row] := Query.FieldByName('datum').AsString;
      Termini.Cells[4, Row] := Query.FieldByName('vreme').AsString;
      Termini.Cells[5, Row] := Query.FieldByName('vakcinacija').AsString;
      Query.next;
      Inc(Row);
    end;

  finally
    Query.Free;
  end;
end;

procedure TfrmSpisakTermina.NazadClick(Sender: TObject);
begin
frmglavnimeni.show;
self.hide;
end;

procedure TfrmSpisakTermina.TerminCellClick(const Column: TColumn;
  const Row: Integer);
begin
SelectedID:= Termini.Cells[0, Row].ToInteger;
end;

procedure TfrmSpisakTermina.UkloniClick(Sender: TObject);
var
  Query: TFDQuery;
begin
Query := TFDQuery.Create(nil);
try
Query.Connection:= GlobalConnection;
Query.SQL.Text := 'DELETE FROM Termin WHERE idtermina = :terminid';
  Query.ParamByName('terminid').AsInteger := SelectedID;
  Query.ExecSQL;
finally
  Query.free;
  showmessage('Termin uklonjen');
  frmspisaktermina.Activate;
end;
end;

end.
