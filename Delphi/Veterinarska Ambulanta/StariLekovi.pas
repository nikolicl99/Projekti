unit StariLekovi;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, System.Rtti,
  FMX.Grid.Style, FMX.Objects, FMX.StdCtrls, FMX.Edit, FMX.Grid, FMX.ScrollBox,
  FMX.ListBox, FMX.Controls.Presentation, FireDAC.Stan.Intf,
  FireDAC.Stan.Option, FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS,
  FireDAC.Phys.Intf, FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt,
  Data.DB, FireDAC.Comp.DataSet, FireDAC.Comp.Client;

type
  TfrmStariLekovi = class(TForm)
    Forma: TPanel;
    ListaLekova: TStringGrid;
    IDLeka_col: TIntegerColumn;
    ImeLeka: TStringColumn;
    Kolicina: TIntegerColumn;
    Nazad: TButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    procedure FormCreate(Sender: TObject);
    procedure NazadClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmStariLekovi: TfrmStariLekovi;

implementation

{$R *.fmx}
uses StariPregled, Main;

procedure TfrmStariLekovi.FormCreate(Sender: TObject);
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

Query.ParamByName('pregledid').AsInteger := frmstaripregled.pregledid;
Query.Open;

    ListaLekova.RowCount := Query.RecordCount;

    Row := 0;
    while not Query.Eof do
    begin
      ListaLekova.Cells[0, Row] := Query.FieldByName('ID').AsString;
      ListaLekova.Cells[1, Row] := Query.FieldByName('ime').AsString;
      ListaLekova.Cells[2, Row] := Query.FieldByName('Kolicina_leka').AsString;
      Query.Next;
      Inc(Row);
    end;
  finally
    Query.Free;
  end;
end;

procedure TfrmStariLekovi.NazadClick(Sender: TObject);
begin
frmStariPregled.show;
self.hide;
end;

end.
