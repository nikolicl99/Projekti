unit Ljubimac_Pregledi;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, System.Rtti,
  FMX.Grid.Style, FMX.Grid, FMX.Controls.Presentation, FMX.ScrollBox,
  FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Param,
  FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf, FireDAC.DApt.Intf,
  FireDAC.Stan.Async, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client, FMX.StdCtrls, FMX.Objects;

type
  Tfrmljubimac_pregledi = class(TForm)
    Pregledi: TStringGrid;
    ID: TIntegerColumn;
    Simptomi: TStringColumn;
    Dijagnoza: TStringColumn;
    Datum: TDateColumn;
    Vreme: TTimeColumn;
    Veterinar: TStringColumn;
    Nazad: TButton;
    Lekovi: TButton;
    Forma: TPanel;
    StyleBook: TStyleBook;
    Navbar: TImage;
    Vakcinacija: TStringColumn;
    procedure FormCreate(Sender: TObject);
    procedure NazadClick(Sender: TObject);
    procedure PreglediCellClick(const Column: TColumn; const Row: Integer);
    procedure LekoviClick(Sender: TObject);
    procedure PreglediCellDblClick(const Column: TColumn; const Row: Integer);
  private
    { Private declarations }
    Vakcinacija_bol: boolean;
  public
    { Public declarations }
    PregledID:integer;

  end;

var
  frmljubimac_pregledi: Tfrmljubimac_pregledi;

implementation

{$R *.fmx}
uses Main, SpisakZivotinja, Pregled_Lekovi, Ljubimci_Vakcine;

procedure Tfrmljubimac_pregledi.FormCreate(Sender: TObject);
var
  Query: TFDQuery;
  Row: Integer;
begin
  Query := TFDQuery.Create(nil);
  try
    Query.Connection := GlobalConnection;
    Query.SQL.Text :=
      'SELECT pregled.idpregleda, termin.vakcinacija, pregled.simptomi, pregled.terapija, termin.datum, termin.vreme, zaposleni.ime, zaposleni.prezime ' +
  'FROM pregled ' +
  'JOIN termin ON pregled.termin = termin.idtermina ' +
  'JOIN zaposleni ON pregled.veterinar = zaposleni.idzaposlenog ' +
  'WHERE termin.ljubimac = :ljubimacid';
  Query.ParamByName('ljubimacid').AsInteger:= frmspisakzivotinja.SelectedID;
    Query.open;
    Pregledi.RowCount := Query.RecordCount;

    Row := 0;
    while not Query.Eof do
    begin
      Pregledi.Cells[0, Row] := Query.FieldByName('idpregleda').AsString;
      Pregledi.Cells[1, Row] := Query.FieldByName('simptomi').AsString;
      Pregledi.Cells[2, Row] := Query.FieldByName('terapija').AsString;
      Pregledi.Cells[3, Row] := Query.FieldByName('datum').AsString;
      Pregledi.Cells[4, Row] := Query.FieldByName('vreme').AsString;
      Pregledi.Cells[5, Row] := Query.FieldByName('ime').AsString + ' ' + Query.FieldByName('prezime').AsString;
      Pregledi.Cells[6, Row] := Query.FieldByName('vakcinacija').AsString;
      Query.Next;
      Inc(Row);
    end;

  finally
    Query.Free;
  end;
end;
   procedure Tfrmljubimac_pregledi.PreglediCellClick(const Column: TColumn; const Row: Integer);
begin
  PregledID := Pregledi.Cells[0, Row].ToInteger;
  Vakcinacija_bol := SameText(Pregledi.Cells[6, Row], 'DA');
end;

procedure Tfrmljubimac_pregledi.PreglediCellDblClick(const Column: TColumn;
  const Row: Integer);
begin
frmLjubimciVakcine.show;
self.hide;
end;

procedure Tfrmljubimac_pregledi.LekoviClick(Sender: TObject);
begin
if Vakcinacija_bol then
    begin
      frmLjubimciVakcine.show;
      self.hide;
    end
    else
    begin
     frmpregled_lekovi.show;
     self.hide;
    end;
end;

procedure Tfrmljubimac_pregledi.NazadClick(Sender: TObject);
begin
frmspisakzivotinja.show;
self.hide;
end;

end.
