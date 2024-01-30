unit Zaposleni_Promocija;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, System.Rtti,
  FMX.Grid.Style, FMX.Objects, FMX.StdCtrls, FMX.Grid, FMX.ScrollBox,
  FMX.ListBox, FMX.Controls.Presentation, FireDAC.Stan.Intf,
  FireDAC.Stan.Option, FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS,
  FireDAC.Phys.Intf, FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt,
  Data.DB, FireDAC.Comp.DataSet, FireDAC.Comp.Client;

type
  TfrmZaposleniPromocije = class(TForm)
    Forma: TPanel;
    Dodaj: TButton;
    Zaposleni_cb: TComboBox;
    Zaposleni: TStringGrid;
    IDZaposlenog_col: TIntegerColumn;
    Ime_col: TStringColumn;
    Prezime_col: TIntegerColumn;
    Zavrsi: TButton;
    Nazad: TButton;
    Zaposleni_Label: TLabel;
    Navbar: TImage;
    StyleBook: TStyleBook;
    procedure FormCreate(Sender: TObject);
    procedure DodajClick(Sender: TObject);
    function GetIDZaposlenog(const Zaposleni: string): Integer;
    procedure NazadClick(Sender: TObject);
    procedure ZavrsiClick(Sender: TObject);
  private
    { Private declarations }
    Ime: string;
    Prezime: string;
    IDZaposlenog: integer;
    Row: integer;
  public
    { Public declarations }
  end;

var
  frmZaposleniPromocije: TfrmZaposleniPromocije;

implementation

{$R *.fmx}
uses main, Vlasnik_Promocija, SpisakPromocija;

procedure TfrmZaposleniPromocije.DodajClick(Sender: TObject);
begin
  Zaposleni.BeginUpdate;
  IDZaposlenog := GetIDZaposlenog(Zaposleni_cb.Selected.text);
  Zaposleni.Cells[0, Row] := inttostr(IDZaposlenog);
  Zaposleni.Cells[1, Row] := Ime;
  Zaposleni.Cells[2, Row] := Prezime;
  Inc(Row);
  Zaposleni.EndUpdate;

  Zaposleni_cb.ItemIndex := -1;
end;

procedure TfrmZaposleniPromocije.FormCreate(Sender: TObject);
var
  MyQuery: TFDQuery;
begin
      MyQuery := TFDQuery.Create(nil);
      try
        MyQuery.Connection := GlobalConnection;
        MyQuery.SQL.Text := 'SELECT Ime, Prezime FROM Zaposleni';
        MyQuery.Open;
        Zaposleni_cb.Items.Clear;
    while not MyQuery.Eof do
    begin
      Zaposleni_cb.Items.Add(MyQuery.FieldByName('Ime').AsString + ' ' + MyQuery.FieldByName('Prezime').AsString);
      MyQuery.Next;
    end;

      finally
          MyQuery.free;
      end;
end;

function TfrmZaposleniPromocije.GetIDZaposlenog(const Zaposleni: string): Integer;
var
  MyQuery: TFDQuery;

begin
  Result := -1;
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT idzaposlenog, Ime, Prezime FROM Zaposleni';
    MyQuery.Open;

    while not MyQuery.Eof do
    begin
      Ime := MyQuery.FieldByName('Ime').AsString;
      Prezime := MyQuery.FieldByName('Prezime').AsString;

      if SameText(Zaposleni, Ime + ' ' + Prezime) then
      begin
        Result := MyQuery.FieldByName('idzaposlenog').AsInteger;
        Exit;
      end;

      MyQuery.Next;
    end;
  finally
    MyQuery.Free;
  end;
end;

procedure TfrmZaposleniPromocije.NazadClick(Sender: TObject);
begin
frmVlasnikPromocije.show;
self.hide;
end;

procedure TfrmZaposleniPromocije.ZavrsiClick(Sender: TObject);
var
  MyQuery: TFDQuery;
  i: Integer;
begin
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'INSERT INTO Zaposleni_Promocije (IDPromocije, IDZaposlenog) VALUES (:idpromocije, :idzaposlenog)';

    for i := 0 to Zaposleni.RowCount - 1 do
    begin
      IDZaposlenog := strtointdef(Zaposleni.Cells[0, i], 0);
      if IDZaposlenog <> 0 then
      begin
      MyQuery.ParamByName('idpromocije').AsInteger := frmpromocije.SelectedID;
      MyQuery.ParamByName('idzaposlenog').AsInteger := IDZaposlenog;
      MyQuery.ExecSQL;

      MyQuery.ParamByName('idpromocije').Clear;
      MyQuery.ParamByName('idzaposlenog').Clear;
      end;
    end;


  finally
    MyQuery.Free;
    ShowMessage('Uneti svi Zaposleni');
    frmpromocije.show;
    self.hide;
  end;
end;

end.
