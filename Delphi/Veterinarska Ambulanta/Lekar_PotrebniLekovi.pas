unit Lekar_PotrebniLekovi;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs,
FMX.Controls.Presentation, FMX.StdCtrls, FMX.Grid.Style,
FMX.Grid, FMX.ScrollBox, FireDAC.Stan.Intf, FireDAC.Stan.Option,
FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf,
FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt, Data.DB,
FireDAC.Comp.DataSet, FireDAC.Comp.Client, FMX.Objects, System.Generics.Collections,
  System.Rtti;

type
  TfrmLekar_Lekovi = class(TForm)
    Forma: TPanel;
    Nazad: TButton;
    Lekovi: TStringGrid;
    Lek: TStringColumn;
    Kolicina: TIntegerColumn;
    Potrebna: TIntegerColumn;
    Cena: TIntegerColumn;
    Navbar: TImage;
    StyleBook: TStyleBook;
    procedure FormCreate(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmLekar_Lekovi: TfrmLekar_Lekovi;
  Row: integer;

implementation

{$R *.fmx}
uses main;

procedure TfrmLekar_Lekovi.FormCreate(Sender: TObject);
var
  Query: TFDQuery;
begin

  Query := TFDQuery.Create(nil);
  try
    Query.Connection := GlobalConnection;
    Query.SQL.Text := 'SELECT * FROM Inventar WHERE Kolicina < Potrebna_Kolicina';
    Query.Open;

    Row := 0;
    while not Query.Eof do
    begin
      Lekovi.Cells[0, Row] := Query.FieldByName('Ime').AsString;
      Lekovi.Cells[1, Row] := Query.FieldByName('Kolicina').AsString;
      Lekovi.Cells[2, Row] := Query.FieldByName('Potrebna_Kolicina').AsString;
      Lekovi.Cells[3, Row] := Query.FieldByName('Cena_Po_Komadu').AsString;

      Query.Next;
      Inc(Row);
    end;
  finally
    Query.Free;
  end;
end;

end.
