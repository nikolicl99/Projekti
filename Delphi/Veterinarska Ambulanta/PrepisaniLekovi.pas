unit PrepisaniLekovi;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs,
  FMX.Controls.Presentation, FMX.StdCtrls, FireDAC.Stan.Intf,
  FireDAC.Stan.Option, FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS,
  FireDAC.Phys.Intf, FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt,
  Data.DB, FireDAC.Comp.DataSet, FireDAC.Comp.Client, FMX.ListBox, System.Rtti,
  FMX.Grid.Style, FMX.Edit, FMX.Grid, FMX.ScrollBox, FMX.Objects;

type
  TfrmPrepisaniLekovi = class(TForm)
    Dodaj: TButton;
    Lekovi: TComboBox;
    ListaLekova: TStringGrid;
    ImeLeka: TStringColumn;
    Kolicina: TIntegerColumn;
    Kolicina_Edit: TEdit;
    Zavrsi: TButton;
    IDLeka_col: TIntegerColumn;
    Nazad: TButton;
    Lek_Label: TLabel;
    Kolicina_Label: TLabel;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    Dostupni_Lekovi: TButton;
    procedure DodajClick(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure ZavrsiClick(Sender: TObject);
    procedure NazadClick(Sender: TObject);
    procedure Dostupni_LekoviClick(Sender: TObject);
  private
    { Private declarations }
IDLeka: integer;
function GetIDLeka(const imeLeka: string): Integer;
  public
    { Public declarations }
  end;

var
  frmPrepisaniLekovi: TfrmPrepisaniLekovi;
  Row: integer = 0;

implementation

{$R *.fmx}
uses pregled, main, termini_veterinar, lekar_potrebnilekovi;

procedure TfrmPrepisaniLekovi.DodajClick(Sender: TObject);
var
imeLeka: string;
begin
  ListaLekova.BeginUpdate;
  imeLeka := Lekovi.Selected.text;
  IDLeka := getIDLeka(imeleka);
  ListaLekova.Cells[0, Row] := inttostr(IDLeka);
  ListaLekova.Cells[1, Row] := Lekovi.selected.text;
  ListaLekova.Cells[2, Row] := Kolicina_Edit.Text;
  Inc(Row);
  ListaLekova.EndUpdate;

  Lekovi.ItemIndex := -1;
  Kolicina_Edit.Text := '';
end;

procedure TfrmPrepisaniLekovi.Dostupni_LekoviClick(Sender: TObject);
begin
frmlekar_lekovi.show;
self.hide;
end;

procedure TfrmPrepisaniLekovi.FormCreate(Sender: TObject);
var
MyQuery: TFDQuery;
begin
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT id, ime FROM inventar WHERE tip = ''LEKOVI''';
    MyQuery.Open;

    while not MyQuery.Eof do
    begin
      Lekovi.Items.AddObject(MyQuery.FieldByName('ime').AsString, TObject(MyQuery.FieldByName('id').AsInteger));
      MyQuery.Next;
    end;
  finally
    MyQuery.Free;
  end;
end;

procedure TfrmPrepisaniLekovi.ZavrsiClick(Sender: TObject);
var
  MyQuery: TFDQuery;
  i: Integer;
  KolicinaBroj: string;
begin
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'INSERT INTO Prepisani_Lekovi (IDPregleda, IDLeka, Kolicina_Leka) VALUES (:idpregleda, :idleka, :kolicinaleka)';

    for i := 0 to ListaLekova.RowCount - 1 do
    begin
      IDLeka := strtointdef(ListaLekova.Cells[0, i], 0);
      if IDLeka <> 0 then
      begin
      KolicinaBroj := ListaLekova.Cells[2, i];
      MyQuery.ParamByName('idpregleda').AsInteger := frmpregled.PregledID;
      MyQuery.ParamByName('idleka').AsInteger := IDLeka;
      MyQuery.ParamByName('kolicinaleka').AsString := KolicinaBroj;
      MyQuery.ExecSQL;

      MyQuery.ParamByName('idleka').Clear;
      MyQuery.ParamByName('kolicinaleka').Clear;
      end;
    end;


  finally
    MyQuery.Free;
    ShowMessage('Uneti svi lekovi');
    frmterminiveterinar.show;
    self.hide;
  end;
end;

function TfrmPrepisaniLekovi.GetIDLeka(const imeLeka: string): Integer;
var
  MyQuery: TFDQuery;
begin
  Result := -1;
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT id FROM inventar WHERE ime = :imeLeka';
    MyQuery.ParamByName('imeLeka').AsString := imeLeka;
    MyQuery.Open;

    if not MyQuery.IsEmpty then
      Result := MyQuery.FieldByName('id').AsInteger;
  finally
    MyQuery.Free;
  end;
end;

procedure TfrmPrepisaniLekovi.NazadClick(Sender: TObject);
begin
frmpregled.show;
self.hide;
end;

end.
