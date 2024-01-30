unit Ljubimci_Vakcine;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, System.Rtti,
  FMX.Grid.Style, FMX.Grid, FMX.Objects, FMX.StdCtrls, FMX.ScrollBox,
  FMX.Controls.Presentation, FireDAC.Stan.Intf, FireDAC.Stan.Option,
  FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf,
  FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt, Data.DB,
  FireDAC.Comp.DataSet, FireDAC.Comp.Client;

type
  TfrmLjubimciVakcine = class(TForm)
    Forma: TPanel;
    Termini: TStringGrid;
    Nazad: TButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Ime: TStringColumn;
    Datum: TStringColumn;
    Revakcinacija: TStringColumn;
    Veterinar: TStringColumn;
    procedure FormCreate(Sender: TObject);
    procedure NazadClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmLjubimciVakcine: TfrmLjubimciVakcine;

implementation

{$R *.fmx}
uses main, spisakzivotinja, Ljubimac_Pregledi;

procedure TfrmLjubimciVakcine.FormCreate(Sender: TObject);
var
  Query: TFDQuery;
  Row: Integer;
begin
  Query := TFDQuery.Create(nil);
  try
    Query.Connection := GlobalConnection;
    Query.SQL.Text :=
      'SELECT Klijent_Vakcinacija.*, Vakcine.*, Pregled.*, Termin.*, Zaposleni.* ' +
      'FROM Klijent_Vakcinacija ' +
      'JOIN Vakcine ON Klijent_Vakcinacija.Vakcina = Vakcine.IDVakcine ' +
      'JOIN Pregled ON Klijent_Vakcinacija.Pregled = Pregled.IDPregleda ' +
      'JOIN Termin ON Pregled.Termin = Termin.IDTermina ' +
      'JOIN Zaposleni ON Pregled.Veterinar = Zaposleni.IDZaposlenog ' +
      'WHERE Klijent_Vakcinacija.Pregled = :PregledID';
    Query.ParamByName('PregledID').AsInteger := frmljubimac_pregledi.PregledID;
    Query.Open;
    Termini.RowCount := Query.RecordCount;

    Row := 0;
    while not Query.Eof do
    begin
      Termini.Cells[0, Row] := Query.FieldByName('imevakcine').AsString;
      Termini.Cells[1, Row] := Query.FieldByName('datum').AsString;
      Termini.Cells[2, Row] := Query.FieldByName('revakcinacija').AsString;
      Termini.Cells[3, Row] := Query.FieldByName('ime').AsString + ' ' + Query.FieldByName('prezime').AsString;
      Query.Next;
      Inc(Row);
    end;
  finally
    Query.Free;
  end;
end;


procedure TfrmLjubimciVakcine.NazadClick(Sender: TObject);
begin
frmLjubimac_Pregledi.show;
self.hide;
end;

end.
