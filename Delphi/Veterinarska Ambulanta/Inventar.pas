unit Inventar;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, System.Rtti,
  FMX.Grid.Style, FMX.Controls.Presentation, FMX.ScrollBox, FMX.Grid,
  FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Param,
  FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf, FireDAC.DApt.Intf,
  FireDAC.Stan.Async, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client, FMX.StdCtrls, FMX.Objects;

type
  TfrmInventar = class(TForm)
    Spisak: TStringGrid;
    TipOpreme: TStringColumn;
    Ime: TStringColumn;
    Opis: TStringColumn;
    Kolicina: TStringColumn;
    MinimalnaKolicina: TStringColumn;
    Odjava: TButton;
    ID: TStringColumn;
    Dobavljaci: TButton;
    Nabavka: TButton;
    Utovar: TButton;
    Lekovi: TButton;
    Image1: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    procedure FormCreate(Sender: TObject);
    procedure OdjavaClick(Sender: TObject);
    procedure DobavljaciClick(Sender: TObject);
    procedure NabavkaClick(Sender: TObject);
    procedure UtovarClick(Sender: TObject);
    procedure LekoviClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmInventar: TfrmInventar;

implementation

{$R *.fmx}
uses Main, LoginZaposleni, SpisakDobavljaca, Nabavka, Utovar, PotrebniLekovi, Dostave;


procedure TfrmInventar.FormCreate(Sender: TObject);
 var
  Query: TFDQuery;
  Row: Integer;

begin
   Query := TFDQuery.Create(nil);

   try
     Query.Connection := GlobalConnection;
    Query.SQL.Text :=
      'SELECT ID, Tip, Ime, Opis, Kolicina, Potrebna_Kolicina FROM Inventar';

      Query.Open;
    Spisak.RowCount := Query.RecordCount;

    Row := 0;
    while not Query.Eof do
    begin
      Spisak.Cells[0, Row] := Query.FieldByName('ID').AsString;
      Spisak.Cells[1, Row] := Query.FieldByName('Tip').AsString;
      Spisak.Cells[2, Row] := Query.FieldByName('Ime').AsString;
      Spisak.Cells[3, Row] := Query.FieldByName('Opis').AsString;
      Spisak.Cells[4, Row] := Query.FieldByName('Kolicina').AsString;
      Spisak.Cells[5, Row] := Query.FieldByName('Potrebna_Kolicina').AsString;

      Query.Next;
      Inc(Row);
    end;
   finally
      Query.Free;
   end;
end;

procedure TfrmInventar.LekoviClick(Sender: TObject);
begin
frmPotrebniLekovi.show;
self.hide;
end;

procedure TfrmInventar.NabavkaClick(Sender: TObject);
begin
frmNabavka.show;
self.Hide;
end;

procedure TfrmInventar.OdjavaClick(Sender: TObject);
begin
frmLogInZaposleni.show;
self.Hide;
end;

procedure TfrmInventar.UtovarClick(Sender: TObject);
begin
frmutovar.show;
self.hide;
end;

procedure TfrmInventar.DobavljaciClick(Sender: TObject);
begin
frmDobavljaci.Show;
self.Hide;
end;

end.
