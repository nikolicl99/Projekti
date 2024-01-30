unit Vlasnik_Promocija;

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
  TfrmVlasnikPromocije = class(TForm)
    Forma: TPanel;
    Dodaj: TButton;
    Klijenti_cb: TComboBox;
    Klijenti: TStringGrid;
    IDKlijenta_col: TIntegerColumn;
    Ime_col: TStringColumn;
    Prezime_col: TIntegerColumn;
    Zavrsi: TButton;
    Nazad: TButton;
    Klijent_Label: TLabel;
    Navbar: TImage;
    StyleBook: TStyleBook;
    procedure FormCreate(Sender: TObject);
    procedure DodajClick(Sender: TObject);
    function GetIDVlasnika(const Vlasnik: string): Integer;
    procedure ZavrsiClick(Sender: TObject);
  private
    { Private declarations }
    IDKlijenta: integer;
    Row: integer;
    Ime, Prezime: string;
  public
    { Public declarations }
  end;

var
  frmVlasnikPromocije: TfrmVlasnikPromocije;

implementation

{$R *.fmx}
uses main, SpisakPromocija, Zaposleni_Promocija;

procedure TfrmVlasnikPromocije.DodajClick(Sender: TObject);
begin
  Klijenti.BeginUpdate;
  IDKlijenta := getIDVlasnika(Klijenti_cb.Selected.text);
  Klijenti.Cells[0, Row] := inttostr(IDKlijenta);
  Klijenti.Cells[1, Row] := Ime;
  Klijenti.Cells[2, Row] := Prezime;
  Inc(Row);
  Klijenti.EndUpdate;

  Klijenti_cb.ItemIndex := -1;
end;

procedure TfrmVlasnikPromocije.FormCreate(Sender: TObject);
var
  MyQuery: TFDQuery;
begin
      MyQuery := TFDQuery.Create(nil);
      try
        MyQuery.Connection := GlobalConnection;
        MyQuery.SQL.Text := 'SELECT ImeVlasnika, PrezimeVlasnika FROM Vlasnici';
        MyQuery.Open;
        Klijenti_cb.Items.Clear;
    while not MyQuery.Eof do
    begin
      Klijenti_cb.Items.Add(MyQuery.FieldByName('ImeVlasnika').AsString + ' ' + MyQuery.FieldByName('PrezimeVlasnika').AsString);
      MyQuery.Next;
    end;

      finally
          MyQuery.free;
      end;
end;
function TfrmVlasnikPromocije.GetIDVlasnika(const Vlasnik: string): Integer;
var
  MyQuery: TFDQuery;

begin
  Result := -1;
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT idvlasnika, ImeVlasnika, PrezimeVlasnika FROM vlasnici';
    MyQuery.Open;

    while not MyQuery.Eof do
    begin
      Ime := MyQuery.FieldByName('ImeVlasnika').AsString;
      Prezime := MyQuery.FieldByName('PrezimeVlasnika').AsString;

      if SameText(Vlasnik, Ime + ' ' + Prezime) then
      begin
        Result := MyQuery.FieldByName('idvlasnika').AsInteger;
        Exit;
      end;

      MyQuery.Next;
    end;
  finally
    MyQuery.Free;
  end;
end;

procedure TfrmVlasnikPromocije.ZavrsiClick(Sender: TObject);
var
  MyQuery: TFDQuery;
  i: Integer;
begin
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'INSERT INTO Klijenti_Promocije (IDPromocije, IDKlijenta) VALUES (:idpromocije, :idvlasnika)';

    for i := 0 to Klijenti.RowCount - 1 do
    begin
      IDKlijenta := strtointdef(Klijenti.Cells[0, i], 0);
      if IDKlijenta <> 0 then
      begin
      MyQuery.ParamByName('idpromocije').AsInteger := frmpromocije.SelectedID;
      MyQuery.ParamByName('idvlasnika').AsInteger := IDKlijenta;
      MyQuery.ExecSQL;

      MyQuery.ParamByName('idpromocije').Clear;
      MyQuery.ParamByName('idvlasnika').Clear;
      end;
    end;


  finally
    MyQuery.Free;
    ShowMessage('Uneti svi vlasnici');
    frmzaposlenipromocije.show;
    self.hide;
  end;
end;

end.
