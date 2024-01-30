unit SpisakZivotinja;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.Layouts,
  FMX.ListBox, FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Param,
  FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf, FireDAC.DApt.Intf,
  FireDAC.Stan.Async, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client, FireDAC.UI.Intf, FireDAC.Stan.Def, FireDAC.Stan.Pool,
  FireDAC.Phys, FireDAC.Phys.SQLite, FireDAC.Phys.SQLiteDef,
  FireDAC.Stan.ExprFuncs, FireDAC.Phys.SQLiteWrapper.Stat, FireDAC.FMXUI.Wait,
  System.Rtti, System.Bindings.Outputs, Fmx.Bind.Editors, Data.Bind.Controls,
  Data.Bind.EngExt, Fmx.Bind.DBEngExt, Data.Bind.Components, Fmx.Bind.Navigator,
  Data.Bind.DBScope, FireDAC.Comp.UI, Data.Bind.Grid, FMX.Controls.Presentation,
  FMX.StdCtrls, Datasnap.Provider, Datasnap.DBClient, FireDAC.Comp.BatchMove,
  FireDAC.Comp.BatchMove.DataSet, FMX.Edit, FMX.Grid.Style, FMX.Grid,
  FMX.ScrollBox, FMX.Objects;

type
  TfrmSpisakZivotinja = class(TForm)
    Nazad: TButton;
    Spisak: TStringGrid;
    ID: TIntegerColumn;
    Ime: TStringColumn;
    Vrsta: TStringColumn;
    Starost: TIntegerColumn;
    Pol: TStringColumn;
    Pregled: TButton;
    Ukloni: TButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    Dodaj: TButton;
    Rasa: TStringColumn;
    Vakcine: TButton;
    procedure NazadClick(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure SpisakCellClick(const Column: TColumn; const Row: Integer);
    procedure PregledClick(Sender: TObject);
    procedure UkloniClick(Sender: TObject);
    procedure DodajClick(Sender: TObject);
    procedure VakcineClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
    UlogovaniVlasnikID : Integer;
    SelectedID: integer;
    SelectedIme: string;
    SelectedVrsta: string;
    SelectedStarost: string;
    SelectedPol: string;
  end;

var
  frmSpisakZivotinja: TfrmSpisakZivotinja;

implementation

{$R *.fmx}
uses Main, LoginForma, Vlasnik_GlavnaForma, Ljubimac_Pregledi, DodavanjeLjubimca, SpisakVakcina;

procedure TfrmSpisakZivotinja.DodajClick(Sender: TObject);
begin
frmDodavanjeLjubimca.Show;
self.hide;
end;

procedure TfrmSpisakZivotinja.FormCreate(Sender: TObject);
var
  Query: TFDQuery;
  Row: Integer;
begin
  Query := TFDQuery.Create(nil);
  try
    Query.Connection := GlobalConnection;
    Query.SQL.Text :=
  'SELECT rasa_ljubimca.idrase, rasa_ljubimca.rasa, ljubimci.idljubimca, ljubimci.imeljubimca, tipovi_ljubimaca.ime_tipa, ljubimci.starost, ljubimci.pol ' +
  'FROM Ljubimci ' +
  'JOIN rasa_ljubimca ON ljubimci.rasa = rasa_ljubimca.idrase ' +
  'JOIN tipovi_ljubimaca ON rasa_ljubimca.tipljubimca = tipovi_ljubimaca.idtipa ' +
  'WHERE ljubimci.vlasnik = :vlasnik';

  Query.ParamByName('vlasnik').AsInteger := frmLogIn.UlogovaniVlasnikID;
    Query.Open;

    Spisak.RowCount := Query.RecordCount;

    Row := 0;
    while not Query.Eof do
    begin
      Spisak.Cells[0, Row] := Query.FieldByName('idljubimca').AsString;
      Spisak.Cells[1, Row] := Query.FieldByName('imeljubimca').AsString;
      Spisak.Cells[2, Row] := Query.FieldByName('ime_tipa').AsString;
      Spisak.Cells[3, Row] := Query.FieldByName('rasa').AsString;
      Spisak.Cells[4, Row] := Query.FieldByName('starost').AsString;
      Spisak.Cells[5, Row] := Query.FieldByName('pol').AsString;
      Query.Next;
      Inc(Row);
    end;

  finally
    Query.Free;
  end;
end;

procedure TfrmSpisakZivotinja.NazadClick(Sender: TObject);
begin
frmGlavniMeni.show;
self.hide;
end;
procedure TfrmSpisakZivotinja.PregledClick(Sender: TObject);
begin
frmljubimac_pregledi.show;
self.hide;
end;

procedure TfrmSpisakZivotinja.SpisakCellClick(const Column: TColumn;
  const Row: Integer);
begin
SelectedID:= Spisak.Cells[0, Row].ToInteger;
SelectedIme:=  Spisak.Cells[1, Row];
SelectedVrsta:=  Spisak.Cells[2, Row];
SelectedStarost:=  Spisak.Cells[3, Row];
SelectedPol:=  Spisak.Cells[4, Row];
end;

procedure TfrmSpisakZivotinja.UkloniClick(Sender: TObject);
var
  Query: TFDQuery;
begin
Query := TFDQuery.Create(nil);
try
Query.Connection:= GlobalConnection;
Query.SQL.Text := 'DELETE FROM Ljubimci WHERE idljubimca = :ljubimacid';
  Query.ParamByName('ljubimacid').AsInteger := SelectedID;
  Query.ExecSQL;
finally
  Query.free;
  showmessage('Ljubimac uklonjen');
  frmspisakzivotinja.Activate;
end;
end;

procedure TfrmSpisakZivotinja.VakcineClick(Sender: TObject);
begin
frmSpisakVakcina.show;
self.hide;
end;

end.
