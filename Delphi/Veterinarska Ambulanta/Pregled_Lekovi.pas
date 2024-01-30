unit Pregled_Lekovi;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs,
  FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Param,
  FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf, FireDAC.DApt.Intf,
  FireDAC.Stan.Async, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client, System.Rtti, FMX.Grid.Style, FMX.StdCtrls, FMX.Grid,
  FMX.Controls.Presentation, FMX.ScrollBox, FMX.Objects;

type
  Tfrmpregled_lekovi = class(TForm)
    Lekovi: TStringGrid;
    ID: TIntegerColumn;
    ImeLeka: TStringColumn;
    Kolicina: TIntegerColumn;
    Cena: TIntegerColumn;
    Dostupnost: TStringColumn;
    Nazad: TButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    procedure FormCreate(Sender: TObject);
    procedure NazadClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmpregled_lekovi: Tfrmpregled_lekovi;

implementation

{$R *.fmx}
uses main, ljubimac_pregledi;

procedure Tfrmpregled_lekovi.FormCreate(Sender: TObject);
var
  Query: TFDQuery;
  Row: Integer;
begin
  Query := TFDQuery.Create(nil);
  try
    Query.Connection := GlobalConnection;
    Query.SQL.Text :=
  'SELECT inventar.id, inventar.ime, prepisani_lekovi.kolicina_leka, inventar.cena_po_komadu, inventar.kolicina, inventar.potrebna_kolicina ' +
  'FROM inventar ' +
  'JOIN prepisani_lekovi ON inventar.id = prepisani_lekovi.idleka ' +
  'WHERE prepisani_lekovi.idpregleda = :pregledid';

Query.ParamByName('pregledid').AsInteger := frmljubimac_pregledi.pregledid;
Query.Open;

Lekovi.RowCount := Query.RecordCount;

Row := 0;
while not Query.Eof do
begin
  Lekovi.Cells[0, Row] := Query.FieldByName('id').AsString;
  Lekovi.Cells[1, Row] := Query.FieldByName('ime').AsString;
  Lekovi.Cells[2, Row] := Query.FieldByName('kolicina_leka').AsString;
  Lekovi.Cells[3, Row] := Query.FieldByName('cena_po_komadu').AsString;

  if Query.FieldByName('kolicina').AsFloat >= Query.FieldByName('potrebna_kolicina').AsFloat then
    Lekovi.Cells[4, Row] := 'Dostupno'
  else
    Lekovi.Cells[4, Row] := 'Nije dostupno';

  Query.Next;
  Inc(Row);
end;
  finally
    Query.Free;
  end;
end;

procedure Tfrmpregled_lekovi.NazadClick(Sender: TObject);
begin
frmljubimac_pregledi.show;
self.hide;
end;

end.
