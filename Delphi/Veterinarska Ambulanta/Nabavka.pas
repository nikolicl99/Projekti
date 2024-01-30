unit Nabavka;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, System.Rtti,
  FMX.Grid.Style, FMX.Grid, FMX.Controls.Presentation, FMX.ScrollBox,
  FMX.StdCtrls, FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Param,
  FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf, FireDAC.DApt.Intf,
  FireDAC.Stan.Async, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client, FMX.Objects, FMX.ListBox;

type
  TfrmNabavka = class(TForm)
    Spisak: TStringGrid;
    IDNabavke: TStringColumn;
    Tip: TStringColumn;
    Naziv: TStringColumn;
    Dobavljac: TStringColumn;
    Kolicina: TStringColumn;
    Datum: TDateColumn;
    Nazad: TButton;
    Poruci: TButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    Proizvod: TComboBox;
    Proizvod_Label: TLabel;
    procedure FormCreate(Sender: TObject);
    procedure NazadClick(Sender: TObject);
    procedure SpisakCellClick(const Column: TColumn; const ARow: Integer);
    procedure PoruciClick(Sender: TObject);
    procedure ProizvodChange(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
    SelectedID: integer;
    SelectedTip: string;
    SelectedNaziv: string;
    SelectedDobavljac: string;
    SelectedDatum: TDate;
    SelectedKolicina: string;
  end;

var
  frmNabavka: TfrmNabavka;

implementation

{$R *.fmx}
uses Main, Inventar, Porucivanje;

procedure TfrmNabavka.FormCreate(Sender: TObject);
    var
  MyQuery: TFDQuery;
//  Row: Integer;
begin
      MyQuery := TFDQuery.Create(nil);
      try
        MyQuery.Connection := GlobalConnection;
        MyQuery.SQL.Text := 'SELECT Ime FROM Inventar';
        MyQuery.Open;
        Proizvod.Items.Clear;
    while not MyQuery.Eof do
    begin
      Proizvod.Items.Add(MyQuery.FieldByName('Ime').AsString);
      MyQuery.Next;
    end;
//
//
//       Query.SQL.Text := 'SELECT Nabavka.IDNabavke, Inventar.Tip, Inventar.Ime, Dobavljaci.Ime || " " || Dobavljaci.Prezime AS ImePrezime, Nabavka.Datum_Nabavke, Nabavka.Kolicina ' +
//            'FROM Nabavka ' +
//            'JOIN Inventar ON Nabavka.IDProizvoda = Inventar.ID ' +
//            'JOIN Dobavljaci ON Nabavka.IDDobavljaca = Dobavljaci.IDDobavljaca';
//
//           Query.Open;
//      Spisak.RowCount := Query.RecordCount;
//
//       Row := 0;
//
//      while not Query.Eof do
//      begin
//        Spisak.Cells[0, Row] := Query.FieldByName('IDNabavke').AsString;
//        Spisak.Cells[1, Row] := Query.FieldByName('Tip').AsString;
//        Spisak.Cells[2, Row] := Query.FieldByName('Ime').AsString;
//        Spisak.Cells[3, Row] := Query.FieldByName('ImePrezime').AsString;
//        Spisak.Cells[4, Row] := Query.FieldByName('Datum_Nabavke').AsString;
//        Spisak.Cells[5, Row] := Query.FieldByName('Kolicina').AsString;
//
//        Query.Next;
//        Inc(Row);
//      end;
      finally
          MyQuery.Free;
      end;
end;

 procedure TfrmNabavka.SpisakCellClick(const Column: TColumn; const ARow: Integer);
 begin
    SelectedID := Spisak.Cells[0, ARow].ToInteger;
    SelectedTip := Spisak.Cells[1, ARow];
    SelectedNaziv := Spisak.Cells[2, ARow];
    SelectedDobavljac := Spisak.Cells[3, ARow];
    SelectedDatum := StrToDate(Spisak.Cells[4, ARow]);
    SelectedKolicina := Spisak.Cells[5, ARow];
 end;

procedure TfrmNabavka.NazadClick(Sender: TObject);
begin
frmInventar.Show;
self.Hide;
end;

procedure TfrmNabavka.PoruciClick(Sender: TObject);
begin
  frmPoruci.Show;
  self.Hide;
end;

procedure TfrmNabavka.ProizvodChange(Sender: TObject);
var
  Query: TFDQuery;
  Row: Integer;
begin
  Query := TFDQuery.Create(nil);
  try
    Query.Connection := GlobalConnection;
    Query.SQL.Text := 'SELECT Nabavka.IDNabavke, Inventar.Tip, Inventar.Ime, Dobavljaci.Ime || " " || Dobavljaci.Prezime AS ImePrezime, Nabavka.Datum_Nabavke, Nabavka.Kolicina ' +
  'FROM Nabavka ' +
  'JOIN Inventar ON Nabavka.IDProizvoda = Inventar.ID ' +
  'JOIN Dobavljaci ON Nabavka.IDDobavljaca = Dobavljaci.IDDobavljaca ' +
  'WHERE Inventar.Ime = :Proizvod '+
  'ORDER BY julianday(substr(Nabavka.Datum_Nabavke, 7, 4) || "-" || substr(Nabavka.Datum_Nabavke, 4, 2) || "-" || substr(Nabavka.Datum_Nabavke, 1, 2)) DESC';
    Query.ParamByName('Proizvod').AsString := Proizvod.Selected.Text;
    Query.Open;

    Spisak.RowCount := Query.RecordCount;

    Row := 0;
    while not Query.Eof do
    begin
      Spisak.Cells[0, Row] := Query.FieldByName('IDNabavke').AsString;
      Spisak.Cells[1, Row] := Query.FieldByName('Tip').AsString;
      Spisak.Cells[2, Row] := Query.FieldByName('Ime').AsString;
      Spisak.Cells[3, Row] := Query.FieldByName('ImePrezime').AsString;
      Spisak.Cells[4, Row] := Query.FieldByName('Datum_Nabavke').AsString;
      Spisak.Cells[5, Row] := Query.FieldByName('Kolicina').AsString;

      Query.Next;
      Inc(Row);
    end;
  finally
    Query.Free;
  end;
end;

end.
